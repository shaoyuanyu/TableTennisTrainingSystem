@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.competition.Competition
import io.github.shaoyuanyu.ttts.dto.competition.CompetitionQueryRequest
import io.github.shaoyuanyu.ttts.dto.table.TableOccupiedByGroup
import io.github.shaoyuanyu.ttts.dto.table.TableStatus
import io.github.shaoyuanyu.ttts.exceptions.NotFoundException
import io.github.shaoyuanyu.ttts.persistence.competition.*
import io.github.shaoyuanyu.ttts.persistence.table.TableEntity
import io.github.shaoyuanyu.ttts.persistence.table.TableTable
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CompetitionService(
    private val database: Database,
    private val studentService: StudentService,
) {
    /**
     * 创建比赛
     *
     * @param name 比赛名称
     * @param type 比赛类型
     * @param date 比赛日期
     * @param registrationDeadline 报名截止日期
     * @param fee 报名费用
     * @param description 比赛描述
     * @return 创建的比赛ID
     */
    fun createCompetition(
        name: String,
        type: String,
        campusId: Int,
        date: LocalDate,
        registrationDeadline: LocalDate,
        fee: Float,
        description: String
    ) {
        // 参数验证
        if (name.isBlank()) {
            throw IllegalArgumentException("比赛名称不能为空")
        }
        
        if (type.isBlank()) {
            throw IllegalArgumentException("比赛类型不能为空")
        }
        
        if (date < Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Shanghai")).date) {
            throw IllegalArgumentException("比赛日期不能早于当前日期")
        }
        
        if (registrationDeadline > date) {
            throw IllegalArgumentException("报名截止日期不能晚于比赛日期")
        }
        
        if (fee < 0) {
            throw IllegalArgumentException("报名费用不能为负数")
        }
        
        transaction(database) {
            CompetitionEntity.new {
                this.name = name
                this.type = type
                this.campusId = campusId
                this.date = date
                this.registrationDeadline = registrationDeadline
                this.fee = fee
                this.description = description
            }
        }
    }

    /**
     * 根据用户ID查询同校区的比赛信息
     *
     * @param userId 用户ID
     * @return 同校区的比赛列表
     */
    fun queryCampusCompetition(userId: String): List<Competition> = transaction(database) {
        val campusId = UserEntity.findById(UUID.fromString(userId))?.campusId ?: throw NotFoundException("用户不存在")
        CompetitionEntity.find {
            CompetitionTable.campusId eq campusId
        }.toList().expose()
    }

    /**
     * 查询所有比赛信息
     *
     * @return 包含所有比赛信息的列表
     */
    fun queryAllCompetitions(): List<Competition> {
        return CompetitionEntity.all().toList().expose()
    }

    /**
     * 报名参加比赛
     */
    fun signupCompetition(userId: String, group: String) {
        transaction(database) {
            // 验证小组名称是否有效
            val validGroups = listOf("A", "B", "C")
            if (group !in validGroups) {
                throw IllegalStateException("无效的小组名称，必须是A、B或C")
            }

            // 检查用户是否已经报名
            val existingSignup = CompetitionSignupEntity.find { CompetitionSignupTable.userId eq userId }
                .limit(1)
                .singleOrNull()

            if (existingSignup != null) {
                throw IllegalStateException("用户已经报名过比赛")
            }

            // 获取用户信息
            val userEntity = UserEntity.findById(UUID.fromString(userId))
                ?: throw NotFoundException("用户不存在")

            val userCampusId = userEntity.campusId

            // 1. 首先查找已经有一个同组人占用的球台（优先分配）
            val partiallyOccupiedTables = TableEntity.find {
                (TableTable.status eq TableStatus.PARTLY_OCCUPIED) and
                        (TableTable.campusId eq userCampusId) and
                        (TableTable.group eq TableOccupiedByGroup.valueOf(group))
            }.toList()

            val selectedTable: TableEntity
            val tableId: Int
            val newTableStatus: TableStatus

            if (partiallyOccupiedTables.isNotEmpty()) {
                // 优先选择部分占用的球台
                selectedTable = partiallyOccupiedTables.random()
                tableId = selectedTable.indexInCampus
                newTableStatus = TableStatus.OCCUPIED // 分配第二个人后，球台满员
            } else {
                // 如果没有部分占用的球台，选择空闲球台
                val availableTables = TableEntity.find {
                    (TableTable.status eq TableStatus.FREE) and
                            (TableTable.campusId eq userCampusId)
                }.toList()

                if (availableTables.isEmpty()) {
                    throw IllegalStateException("当前校区没有可用球台")
                }

                selectedTable = availableTables.random()
                tableId = selectedTable.indexInCampus
                newTableStatus = TableStatus.PARTLY_OCCUPIED // 分配第一个人，部分占用
            }

            // 插入报名记录
            CompetitionSignupEntity.new {
                this.userId = userId
                username = userEntity.username
                this.group = group
                this.tableId = tableId
                campusId = userCampusId
                status = "ACTIVE"
                createdAt = Clock.System.now()
            }

            studentService.deduct(userId, 30.0f) // 报名费30元

            // 更新球台状态
            val tableEntity = TableEntity.find{
                (TableTable.index_in_campus eq selectedTable.indexInCampus) and
                        (TableTable.campusId eq userCampusId)
            }.limit(1).singleOrNull()
                ?: throw NotFoundException("球台不存在")

            tableEntity.status = newTableStatus
            tableEntity.group = TableOccupiedByGroup.valueOf(group)

            USER_LOGGER.info("报名成功！小组：$group，分配球台：$tableId")
        }
    }

    /**
     * 查询报名信息
     */
    fun querySignup(userId: String): CompetitionQueryRequest =
        transaction(database) {
            // 1. 先查询自己的报名信息
            val mySignup = CompetitionSignupEntity.find {
                (CompetitionSignupTable.userId eq userId)and
                        (CompetitionSignupTable.status eq "ACTIVE")
            }
                .limit(1)
                .singleOrNull()
                ?: throw NotFoundException("您没有未完成的比赛")

            val myTableId = mySignup.tableId
            val myGroup = mySignup.group
            val myUsername = mySignup.username
            val myCampusId = mySignup.campusId

            // 2. 查询同一球台的所有报名信息（排除自己）
            val opponentSignups = CompetitionSignupEntity.find {
                (CompetitionSignupTable.tableId eq myTableId) and
                        (CompetitionSignupTable.userId neq userId) and
                        (CompetitionSignupTable.campusId eq myCampusId)
            }.toList()

            // 3. 获取对手的用户名（可能有多个对手，取第一个）
            val opponentUsername = opponentSignups.firstOrNull()?.username

            // 4. 构建返回结果
            val signupInfo = CompetitionQueryRequest(
                tableId = myTableId,
                group = myGroup,
                myUsername = myUsername,
                opponentUsername = opponentUsername
            )

            USER_LOGGER.info("查询报名信息成功，用户 ID：$userId，球台 ID：$myTableId，组别：$myGroup，对手用户名：${opponentUsername ?: "无"}")

            signupInfo
        }

//    /**
//     * 获取所有比赛信息
//     */
//    fun getAllCompetitions(): List<CompetitionOneMatchInfo> =
//        transaction(database) {
//            // 1. 查询所有比赛记录
//            val allCompetitions = CompetitionSignupEntity.all()
//                .filter { it.status == "ACTIVE" }
//                .toList()
//
//            if (allCompetitions.isEmpty()) {
//                throw NotFoundException("当前没有任何比赛报名")
//            }
//
//            // 2. 按复合键（校区+球桌）分组
//            val competitionsByCompositeKey = allCompetitions
//                .groupBy { "${it.campusId}-${it.tableId}" }  // 创建复合键
//
//            // 3. 构建比赛信息列表
//            competitionsByCompositeKey.mapNotNull { (compositeKey, signups) ->
//                // 从复合键中解析出校区ID和球桌ID
//                val parts = compositeKey.split("-")
//                val campusId = parts[0].toInt()  // 转换为Int类型
//                val tableId = parts[1].toInt()
//
//                when (signups.size) {
//                    1 -> {
//                        val player = signups[0]
//                        CompetitionOneMatchInfo(
//                            tableId = tableId,
//                            campusId = campusId,
//                            group = player.group,
//                            player1Username = player.username,
//                            player2Username = "等待对手"
//                        )
//                    }
//                    in 2..Int.MAX_VALUE -> {
//                        val player1 = signups[0]
//                        val player2 = signups[1]
//                        CompetitionOneMatchInfo(
//                            tableId = tableId,
//                            campusId = campusId,
//                            group = player1.group,
//                            player1Username = player1.username,
//                            player2Username = player2.username
//                        )
//                    }
//                    else -> null
//                }
//            }
//        }

//    /**
//     * 获取管理员所在校区的比赛的每轮信息
//     */
//    fun getCampusCompetitionMatchs(userId: String): List<CompetitionOneMatchInfo> =
//        transaction(database) {
//            // 获取用户信息
//            val userEntity = UserEntity.findById(UUID.fromString(userId))
//                ?: throw NotFoundException("用户不存在")
//            val campus=userEntity.campusId
//            val allCompetitions = CompetitionSignupEntity.all()
//                .filter { (it.campusId == campus)and(it.status == "ACTIVE") }
//                .toList()
//            if(allCompetitions.isEmpty()) {
//                throw NotFoundException("当前没有任何比赛报名")
//            }
//            // 2. 按球桌分组，找出每个球桌的比赛双方
//            val competitionsByTable = allCompetitions.groupBy { it.tableId }
//
//            // 3. 构建比赛信息列表
//            competitionsByTable.mapNotNull { (tableId, signups) ->
//                if (signups.size >= 2) {
//                    // 有至少两人报名，形成比赛
//                    val player1 = signups[0]
//                    val player2 = signups[1]
//
//                    CompetitionOneMatchInfo(
//                        tableId = tableId,
//                        campusId = player1.campusId, // 同一球台的用户在同一校区
//                        group = player1.group,       // 同一球台的用户在同一小组
//                        player1Username = player1.username,
//                        player2Username = player2.username
//                    )
//                } else if (signups.size == 1) {
//                    // 只有一人报名，显示等待对手
//                    val player = signups[0]
//                    CompetitionOneMatchInfo(
//                        tableId = tableId,
//                        campusId = player.campusId,
//                        group = player.group,
//                        player1Username = player.username,
//                        player2Username = "等待对手"
//                    )
//                } else {
//                    null // 空球台，跳过
//                }
//            }
//        }

    fun enterResults(winner:String,loser:String){
        transaction(database) {
            val winnerEntity=UserEntity.find{ UserTable.username eq winner}.limit(1).singleOrNull()
                ?:throw NotFoundException("获胜者不存在")
            val loserEntity=UserEntity.find{UserTable.username eq loser}.limit(1).singleOrNull()
                ?:throw NotFoundException("失败者不存在")
            val winnerSignup=CompetitionSignupEntity.find{
                (CompetitionSignupTable.userId eq winnerEntity.id.value.toString())and
                        (CompetitionSignupTable.status eq "ACTIVE")
            }.limit(1).singleOrNull()
                ?:throw NotFoundException("获胜者没有未完成的比赛")
            val loserSignup=CompetitionSignupEntity.find{
                (CompetitionSignupTable.userId eq loserEntity.id.value.toString())and
                        (CompetitionSignupTable.status eq "ACTIVE")
            }.limit(1).singleOrNull()
                ?:throw NotFoundException("失败者没有未完成的比赛")
            if(winnerSignup.tableId!=loserSignup.tableId){
                throw IllegalArgumentException("两人不在同一球台，无法录入比赛结果")
            }
            // 更新比赛状态为 COMPLETED
            winnerSignup.status="COMPLETED"
            loserSignup.status="COMPLETED"
            // 释放球台
            val tableEntity= TableEntity.find{
                (TableTable.index_in_campus eq winnerSignup.tableId) and
                        (TableTable.campusId eq winnerSignup.campusId)
            }
            //更新球台状态为FREE
            tableEntity.map{
                it.status = TableStatus.FREE
                it.group = TableOccupiedByGroup.FREE
            }
            USER_LOGGER.info("比赛结果录入成功，获胜者：$winner，失败者：$loser，球台 ID：${winnerSignup.tableId}")
        }
    }

    /**
     * 获取用户个人比赛安排
     */
    fun getUserSchedule(userId: String): List<Map<String, Any>> {
        return transaction(database) {
            // 获取用户信息
            val userEntity = UserEntity.findById(UUID.fromString(userId))
                ?: throw NotFoundException("用户不存在")

            // 获取用户的报名信息
            val signup = CompetitionSignupEntity.find {
                (CompetitionSignupTable.userId eq userId) and
                        (CompetitionSignupTable.status eq "ACTIVE")
            }.singleOrNull() ?: throw NotFoundException("用户未报名比赛")

            // 获取同组所有用户
            val groupMembers = CompetitionSignupEntity.find {
                (CompetitionSignupTable.group eq signup.group) and
                        (CompetitionSignupTable.campusId eq userEntity.campusId) and
                        (CompetitionSignupTable.status eq "ACTIVE")
            }.toList()

            // 生成循环赛程
            val players = groupMembers.map { it.username }
            val matches = generateRoundRobinSchedule(players)

            // 筛选出包含当前用户的所有比赛
            val userMatches = matches.filter { match ->
                match["player1"] == signup.username || match["player2"] == signup.username
            }

            userMatches
        }
    }

    /**
     * 生成循环赛程
     */
    private fun generateRoundRobinSchedule(players: List<String>): List<Map<String, Any>> {
        val n = if (players.size % 2 == 0) players.size else players.size + 1
        val fixedPlayers = if (players.size % 2 == 1) players + listOf("轮空") else players
        val rounds = n - 1
        val matchesPerRound = n / 2

        val allMatches = mutableListOf<Map<String, Any>>()

        for (round in 1..rounds) {
            for (matchIndex in 0 until matchesPerRound) {
                val player1Index = if (matchIndex == 0) 0 else (round + matchIndex - 1) % (n - 1) + 1
                val player2Index = if (matchIndex == 0) n - 1 else (round - matchIndex - 1 + n - 1) % (n - 1) + 1

                val player1 = if (player1Index < fixedPlayers.size) fixedPlayers[player1Index] else "轮空"
                val player2 = if (player2Index < fixedPlayers.size) fixedPlayers[player2Index] else "轮空"

                // 确保不是自己和自己比赛（除了轮空的情况）
                if (player1 != player2 || player1 == "轮空") {
                    allMatches.add(
                        mapOf(
                            "round" to round,
                            "player1" to player1,
                            "player2" to player2
                        )
                    )
                }
            }
        }

        return allMatches
    }
}