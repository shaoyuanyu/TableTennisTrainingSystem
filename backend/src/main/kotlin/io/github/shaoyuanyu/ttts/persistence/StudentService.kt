@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.recharge.RechargeRecord
import io.github.shaoyuanyu.ttts.dto.student.ComQueryRequest
import io.github.shaoyuanyu.ttts.dto.student.CompetitionInfo
import io.github.shaoyuanyu.ttts.dto.student.Status
import io.github.shaoyuanyu.ttts.dto.student.Group
import io.github.shaoyuanyu.ttts.exceptions.NotFoundException
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import io.github.shaoyuanyu.ttts.persistence.competition.ComEntity
import io.github.shaoyuanyu.ttts.persistence.competition.ComTable
import io.github.shaoyuanyu.ttts.persistence.recharge.RechargeEntity
import io.github.shaoyuanyu.ttts.persistence.recharge.RechargeTable
import io.github.shaoyuanyu.ttts.persistence.recharge.expose
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import org.jetbrains.exposed.v1.core.and
import io.github.shaoyuanyu.ttts.persistence.table.TableEntity
import io.github.shaoyuanyu.ttts.persistence.table.TableTable
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal val STUDENT_SERVICE_LOGGER: Logger = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.persistence.student")

class StudentService(
    private val database: Database,
    private val userService: UserService
) {
    /**
     * 查询余额
     */
    fun queryBalance(uuid: String): Float =
        transaction(database) {
            StudentEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }
                it.balance
            }.also { newBalance ->
                USER_LOGGER.info("查询用户余额成功，用户 ID：$uuid，余额：${newBalance}")
            }
        }

    /**
     * 更新余额
     */
    fun updateBalance(uuid: String, newBalance: Float) =
        transaction(database) {
            StudentEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }
                it.balance = newBalance
            }.also { newBalance ->
                USER_LOGGER.info("更新用户余额成功，用户 ID：$uuid，余额：${newBalance}")
            }
        }

    /**
     * 充值
     */
    fun recharge(uuid: String, amount: Float) =
        transaction(database) {
            StudentEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }
                it.balance += amount
                RechargeEntity.new {
                    this.userId = uuid
                    this.amount = amount
                    this.createdAt = Clock.System.now()
                }
            }.also { newBalance ->
                USER_LOGGER.info("充值成功，用户 ID：$uuid，余额：${newBalance}")
            }
        }

    /**
     * 扣费
     */
    fun deduct(uuid: String, amount: Float) =
        transaction(database) {
            StudentEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }
                if (it.balance < amount) {
                    throw Exception("余额不足")
                }
                it.balance -= amount
                RechargeEntity.new {
                    this.userId = uuid
                    this.amount = -amount
                    this.createdAt = Clock.System.now()
                }
            }.also { student ->
                USER_LOGGER.info("扣费成功，用户 ID：$uuid，余额：${student}")
            }
        }

    /**
     * 根据campusId查询校区获取校区实体
     */
    fun queryCampusById(campusId: Int): CampusEntity =
        transaction(database) {
            CampusEntity.findById(campusId).let {
                if (it == null) {
                    throw Exception("校区不存在")
                }

                it
            }.also {
                USER_LOGGER.info("查询校区成功，校区 ID：$campusId，校区名称：${it.campusName}")
            }
        }

    /**
     * 获取用户充值历史记录
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return Pair<充值记录列表, 总记录数>
     */
    fun getRechargeHistory(userId: String, page: Int, size: Int): Pair<List<RechargeRecord>, Long> =
        transaction(database) {
            val query = RechargeEntity.find {
                RechargeTable.userId eq userId
            }.toList()
            // 计算偏移量
            val offset = (page - 1) * size

            // 查询总记录数
            val totalCount = query.size.toLong()

            // 查询分页数据
            val records = query
                .sortedByDescending { it.createdAt }
                .drop(offset)
                .take(size)
                .map { it.expose() }


            records to totalCount
        }

    /**
     * 根据username查询用户充值历史记录
     * @param username 用户名
     * @param page 页码
     * @param size 每页大小
     * @return Pair<充值记录列表, 总记录数>
     * @throws Exception 如果用户不存在
     */
    fun getRechargeHistoryByUsername(username:String, page: Int, size: Int): Pair<List<RechargeRecord>, Long> {
        val user = userService.queryUserByUsername(username)
        return getRechargeHistory(user.uuid.toString(), page, size)
    }

    /**
     * 获取所有充值记录（超级管理员）
     * @return 充值记录列表
     */
    fun getAllRechargeRecords(page: Int, size: Int): Pair<List<RechargeRecord>,Long> =
        transaction(database) {

            val query = RechargeEntity.all().toList()

            // 计算偏移量
            val offset = (page - 1) * size

            // 查询总记录数
            val totalCount = query.size.toLong()

            // 查询分页数据
            val records = query
                .sortedByDescending { it.createdAt }
                .drop(offset)
                .take(size)
                .map { it.expose() }
            USER_LOGGER.info("查询充值记录成功")
            records to totalCount
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
            val existingSignup = ComEntity.find { ComTable.userId eq userId }
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
                (TableTable.status eq Status.partlyoccupy) and
                        (TableTable.campusId eq userCampusId) and
                        (TableTable.group eq Group.valueOf(group))
            }.toList()

            val selectedTable: TableEntity
            val tableId: Int
            val newTableStatus: Status

            if (partiallyOccupiedTables.isNotEmpty()) {
                // 优先选择部分占用的球台
                selectedTable = partiallyOccupiedTables.random()
                tableId = selectedTable.indexInCampus
                newTableStatus = Status.occupy // 分配第二个人后，球台满员
            } else {
                // 如果没有部分占用的球台，选择空闲球台
                val availableTables = TableEntity.find {
                    (TableTable.status eq Status.free) and
                            (TableTable.campusId eq userCampusId)
                }.toList()

                if (availableTables.isEmpty()) {
                    throw IllegalStateException("当前校区没有可用球台")
                }

                selectedTable = availableTables.random()
                tableId = selectedTable.indexInCampus
                newTableStatus =Status.partlyoccupy // 分配第一个人，部分占用
            }

            // 插入报名记录
            ComEntity.new {
                this.userId = userId
                username = userEntity.username
                this.group = group
                this.tableId = tableId
                campusId = userCampusId
                status = "ACTIVE"
                createdAt = Clock.System.now()
            }

            deduct(userId, 30.0f) // 报名费30元

            // 更新球台状态
            val tableEntity = TableEntity.find{
                (TableTable.index_in_campus eq selectedTable.indexInCampus) and
                (TableTable.campusId eq userCampusId)
            }.limit(1).singleOrNull()
                ?: throw NotFoundException("球台不存在")

            tableEntity.status = newTableStatus
            tableEntity.group = Group.valueOf(group)

            USER_LOGGER.info("报名成功！小组：$group，分配球台：$tableId")
        }
    }

    /**
     * 查询报名信息
     */
    fun querySignup(userId: String): ComQueryRequest =
        transaction(database) {
                // 1. 先查询自己的报名信息
            val mySignup = ComEntity.find {
                (ComTable.userId eq userId)and
                    (ComTable.status eq "ACTIVE")
            }
                .limit(1)
                .singleOrNull()
                ?: throw NotFoundException("您没有未完成的比赛")

                val myTableId = mySignup.tableId
                val myGroup = mySignup.group
                val myUsername = mySignup.username
                val myCampusId = mySignup.campusId

                // 2. 查询同一球台的所有报名信息（排除自己）
                val opponentSignups = ComEntity.find {
                    (ComTable.tableId eq myTableId) and
                            (ComTable.userId neq userId) and
                            (ComTable.campusId eq myCampusId)
                }.toList()

                // 3. 获取对手的用户名（可能有多个对手，取第一个）
                val opponentUsername = opponentSignups.firstOrNull()?.username

                // 4. 构建返回结果
                val signupInfo = ComQueryRequest(
                    tableId = myTableId,
                    group = myGroup,
                    myUsername = myUsername,
                    opponentUsername = opponentUsername
                )

            USER_LOGGER.info("查询报名信息成功，用户 ID：$userId，球台 ID：$myTableId，组别：$myGroup，对手用户名：${opponentUsername ?: "无"}")

            signupInfo
        }

    /**
     * 获取所有比赛信息
     */
    fun getAllCompetitions(): List<CompetitionInfo> =
        transaction(database) {
            // 1. 查询所有比赛记录
            val allCompetitions = ComEntity.all()
                .filter { it.status == "ACTIVE" }
                .toList()

            if (allCompetitions.isEmpty()) {
                throw NotFoundException("当前没有任何比赛报名")
            }

            // 2. 按复合键（校区+球桌）分组
            val competitionsByCompositeKey = allCompetitions
                .groupBy { "${it.campusId}-${it.tableId}" }  // 创建复合键

            // 3. 构建比赛信息列表
            competitionsByCompositeKey.mapNotNull { (compositeKey, signups) ->
                // 从复合键中解析出校区ID和球桌ID
                val parts = compositeKey.split("-")
                val campusId = parts[0].toInt()  // 转换为Int类型
                val tableId = parts[1].toInt()

                when (signups.size) {
                    1 -> {
                        val player = signups[0]
                        CompetitionInfo(
                            tableId = tableId,
                            campusId = campusId,
                            group = player.group,
                            player1Username = player.username,
                            player2Username = "等待对手"
                        )
                    }
                    in 2..Int.MAX_VALUE -> {
                        val player1 = signups[0]
                        val player2 = signups[1]
                        CompetitionInfo(
                            tableId = tableId,
                            campusId = campusId,
                            group = player1.group,
                            player1Username = player1.username,
                            player2Username = player2.username
                        )
                    }
                    else -> null
                }
            }
        }

    /**
     * 获取管理员所在校区的比赛信息
     */
    fun getCampusCompetitions(userId: String): List<CompetitionInfo> =
        transaction(database) {
            // 获取用户信息
            val userEntity = UserEntity.findById(UUID.fromString(userId))
                ?: throw NotFoundException("用户不存在")
            val campus=userEntity.campusId
            val allCompetitions = ComEntity.all()
                .filter { (it.campusId == campus)and(it.status == "ACTIVE") }
                .toList()
            if(allCompetitions.isEmpty()) {
                throw NotFoundException("当前没有任何比赛报名")
            }
            // 2. 按球桌分组，找出每个球桌的比赛双方
            val competitionsByTable = allCompetitions.groupBy { it.tableId }

            // 3. 构建比赛信息列表
            competitionsByTable.mapNotNull { (tableId, signups) ->
                if (signups.size >= 2) {
                    // 有至少两人报名，形成比赛
                    val player1 = signups[0]
                    val player2 = signups[1]

                    CompetitionInfo(
                        tableId = tableId,
                        campusId = player1.campusId, // 同一球台的用户在同一校区
                        group = player1.group,       // 同一球台的用户在同一小组
                        player1Username = player1.username,
                        player2Username = player2.username
                    )
                } else if (signups.size == 1) {
                    // 只有一人报名，显示等待对手
                    val player = signups[0]
                    CompetitionInfo(
                        tableId = tableId,
                        campusId = player.campusId,
                        group = player.group,
                        player1Username = player.username,
                        player2Username = "等待对手"
                    )
                } else {
                    null // 空球台，跳过
                }
            }
        }

    /**
     * 获取比赛列表（分页）
     */
    fun getTournaments(page: Int, size: Int): Map<String, Any> =
        transaction(database) {
            val offset = (page - 1) * size
            val allTournaments = ComEntity.all().toList()
            val totalCount = allTournaments.size
            val tournaments = allTournaments
                .sortedByDescending { it.createdAt }
                .drop(offset)
                .take(size)
                .map { it.expose() }
            
            mapOf(
                "items" to tournaments,
                "total" to totalCount
            )
        }

    /**
     * 创建比赛
     */
    fun createTournament(request: Map<String, Any>, creatorId: String): ComEntity =
        transaction(database) {
            val name = request["name"] as? String ?: throw IllegalArgumentException("比赛名称不能为空")
            val type = request["type"] as? String ?: throw IllegalArgumentException("比赛类型不能为空")
            val dateStr = request["date"] as? String ?: throw IllegalArgumentException("比赛日期不能为空")
            val registrationDeadlineStr = request["registrationDeadline"] as? String ?: throw IllegalArgumentException("报名截止日期不能为空")
            val fee = (request["fee"] as? Number)?.toFloat() ?: 0f
            val description = request["description"] as? String ?: ""
            
            // 验证日期格式
            try {
                // 这里应该添加日期解析逻辑，为了简化直接存储字符串
                // 实际项目中应该使用 proper date parsing
            } catch (e: Exception) {
                throw IllegalArgumentException("日期格式不正确")
            }
            
            ComEntity.new {
                userId = creatorId
                username = "tournament" // 占位符，实际应该获取创建者用户名
                group = type // 使用type作为组别
                tableId = 0 // 比赛不需要球桌ID
                campusId = 0 // 暂时设置为0
                status = "ACTIVE"
                createdAt = Clock.System.now()
            }.apply {
                // 使用description字段存储费用和截止日期等信息
                // 格式: fee|registrationDeadline|description
                this.description = "$fee|$registrationDeadlineStr|$description"
            }
        }

    /**
     * 删除比赛
     */
    fun deleteTournament(id: Int): Boolean =
        transaction(database) {
            val tournament = ComEntity.findById(id)
            if (tournament != null) {
                tournament.delete()
                true
            } else {
                false
            }
        }

    /**
     * 获取最新比赛信息
     */
    fun getLatestTournament(): Map<String, Any?> =
        transaction(database) {
            val latestTournament = ComEntity.all()
                .filter { it.username == "tournament" } // 筛选出比赛记录
                .maxByOrNull { it.createdAt } // 获取最新的比赛记录
            
            if (latestTournament != null) {
                latestTournament.expose()
            } else {
                throw NotFoundException("未找到比赛信息")
            }
        }

    // 为ComEntity添加expose方法
    fun ComEntity.expose(): Map<String, Any?> = mapOf(
        "id" to id.value,
        "name" to username, // 使用username作为比赛名称
        "type" to group, // 使用group作为比赛类型
        "date" to createdAt.toString(), // 简化处理，实际应该有专门的日期字段
        "status" to status,
        "createdAt" to createdAt.toString()
    ).let { baseMap ->
        // 如果是比赛记录，解析description字段获取详细信息
        if (group in listOf("monthly", "quarterly", "annual")) { // 判断为比赛记录
            val parts = description.split("|")
            val fee = parts.getOrNull(0)?.toFloatOrNull() ?: 30f
            val registrationDeadline = parts.getOrNull(1) ?: ""
            val desc = parts.getOrNull(2) ?: ""
            
            baseMap + mapOf(
                "fee" to fee,
                "registrationDeadline" to registrationDeadline,
                "description" to desc
            )
        } else {
            baseMap + mapOf(
                "fee" to 30,
                "registrationDeadline" to "",
                "description" to ""
            )
        }
    }

    fun enterResults(winner:String,loser:String){
        transaction(database) {
            val winnerEntity=UserEntity.find{ UserTable.username eq winner}.limit(1).singleOrNull()
                ?:throw NotFoundException("获胜者不存在")
            val loserEntity=UserEntity.find{UserTable.username eq loser}.limit(1).singleOrNull()
                ?:throw NotFoundException("失败者不存在")
            val winnerSignup=ComEntity.find{
                (ComTable.userId eq winnerEntity.id.value.toString())and
                        (ComTable.status eq "ACTIVE")
            }.limit(1).singleOrNull()
                ?:throw NotFoundException("获胜者没有未完成的比赛")
            val loserSignup=ComEntity.find{
                (ComTable.userId eq loserEntity.id.value.toString())and
                        (ComTable.status eq "ACTIVE")
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
                it.status=Status.free
                it.group=Group.free
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
            val signup = ComEntity.find { 
                (ComTable.userId eq userId) and 
                (ComTable.status eq "ACTIVE") 
            }.singleOrNull() ?: throw NotFoundException("用户未报名比赛")
            
            // 获取同组所有用户
            val groupMembers = ComEntity.find { 
                (ComTable.group eq signup.group) and 
                (ComTable.campusId eq userEntity.campusId) and
                (ComTable.status eq "ACTIVE")
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
                if (player1 != player2 || player1 == "轮空" || player2 == "轮空") {
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