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
    companion object {
        // 中文组名到枚举的映射
        private fun mapGroupNameToEnum(groupName: String): Group = when (groupName) {
            "甲" -> Group.A
            "乙" -> Group.B  
            "丙" -> Group.C
            else -> throw IllegalArgumentException("无效的组名: $groupName")
        }
    }
    
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
            val validGroups = listOf("甲", "乙", "丙")
            if (group !in validGroups) {
                throw IllegalStateException("无效的小组名称，必须是甲、乙或丙")
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
            val groupEnum = mapGroupNameToEnum(group)
            val partiallyOccupiedTables = TableEntity.find {
                (TableTable.status eq Status.partlyoccupy) and
                        (TableTable.campusId eq userCampusId) and
                        (TableTable.group eq groupEnum)
            }.toList()

            val selectedTable: TableEntity
            val tableIndex: Int
            val newTableStatus: Status

            if (partiallyOccupiedTables.isNotEmpty()) {
                // 优先选择部分占用的球台
                selectedTable = partiallyOccupiedTables.random()
                tableIndex = selectedTable.indexInCampus
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
                tableIndex = selectedTable.indexInCampus
                newTableStatus = Status.partlyoccupy // 分配第一个人，部分占用
            }

            // 插入报名记录
            ComEntity.new {
                this.userId = userId
                username = userEntity.username
                this.group = group
                this.tableId = tableIndex
                campusId = userCampusId
                status = "ACTIVE"
                createdAt = Clock.System.now()
            }

            deduct(userId, 30.0f) // 报名费30元

            // 更新球台状态
            val tableEntity = TableEntity.findById(selectedTable.id.value)
                ?: throw NotFoundException("球台不存在")

            tableEntity.status = newTableStatus
            tableEntity.group = groupEnum

            USER_LOGGER.info("报名成功！小组：$group，分配球台：$tableIndex")
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

                // 2. 查询同一球台的所有报名信息（排除自己）
                val opponentSignups = ComEntity.find {
                    (ComTable.tableId eq myTableId) and (ComTable.userId neq userId)
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
}