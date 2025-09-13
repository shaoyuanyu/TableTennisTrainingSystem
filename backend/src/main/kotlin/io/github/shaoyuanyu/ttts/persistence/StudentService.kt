@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.recharge.RechargeRecord
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.recharge.RechargeEntity
import io.github.shaoyuanyu.ttts.persistence.recharge.RechargeTable
import io.github.shaoyuanyu.ttts.persistence.recharge.expose
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.student_coach.StudentCoachRelationEntity
import io.github.shaoyuanyu.ttts.persistence.student_coach.StudentCoachRelationTable
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.inList
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
                LOGGER.info("查询用户余额成功，用户 ID：$uuid，余额：${newBalance}")
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
                LOGGER.info("更新用户余额成功，用户 ID：$uuid，余额：${newBalance}")
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
                LOGGER.info("充值成功，用户 ID：$uuid，余额：${newBalance}")
            }
        }

    /**
     * 扣费
     */
    fun deductBalance(uuid: String, amount: Float): Float =
        transaction(database) {
            StudentEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }
                if (it.balance < amount) {
                    throw Exception("余额不足")
                }
                it.balance -= amount
                it.balance
            }.also { student ->
                LOGGER.info("扣费成功，用户 ID：$uuid，余额：${student}")
            }
        }
        
    /**
     * 学生申请教练
     */
    fun applyForCoach(studentUUID: String, coachUUID: String, expectedStartTime: Instant, message: String?) =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentUUID))
                ?: throw Exception("学生不存在")
            val coach = CoachEntity.findById(UUID.fromString(coachUUID))
                ?: throw Exception("教练不存在")

            // 检查学生是否已达到最大教练数
            if (student.currentCoach >= student.maxCoach) {
                throw Exception("学生已达到最大可选教练数")
            }

            // 检查教练是否已达到最大学生数
            if (coach.currentStudents >= coach.maxStudents) {
                throw Exception("教练已达到最大可接收学生数")
            }

            // 检查是否存在相同状态的关系
            val existingRelation = StudentCoachRelationEntity.find {
                (StudentCoachRelationTable.student_id eq student.id) and
                        (StudentCoachRelationTable.coach_id eq coach.id) and
                        (StudentCoachRelationTable.status inList listOf("pending", "approved", "active"))
            }.firstOrNull()

            if (existingRelation != null) {
                throw Exception("已存在相同状态的申请或关系")
            }

            // 创建新的申请关系
            val relation = StudentCoachRelationEntity.new {
                this.studentID = student
                this.coachID = coach
                this.status = "pending"
                this.applicationTime = Clock.System.now()
                this.expectedStartTime = expectedStartTime
                this.studentMessage = message
                this.createdAt = Clock.System.now()
                this.updatedAt = Clock.System.now()
            }

            STUDENT_SERVICE_LOGGER.info("学生申请教练成功")

            relation
        }

    /**
     * 获取学生申请记录
     */
    fun getStudentApplications(studentUUID: String, status: String?, page: Int, size: Int): List<StudentCoachRelationEntity> =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentUUID))
                ?: throw Exception("学生不存在")

            val query = if (status != null) {
                StudentCoachRelationEntity.find {
                    (StudentCoachRelationTable.student_id eq student.id) and
                            (StudentCoachRelationTable.status eq status)
                }
            } else {
                StudentCoachRelationEntity.find {
                    StudentCoachRelationTable.student_id eq student.id
                }
            }

            // 计算偏移量
            val offset = (page - 1) * size

            // 查询分页数据
            query
                .sortedByDescending { it.applicationTime }
                .drop(offset)
                .take(size)
                .toList()
        }

    /**
     * 选择教练课程（保留原有功能作为管理员直接建立关系使用）
     */
    fun selectCourse(studentUUID: String, coachUUID: String, startTime: LocalDateTime, endTime: LocalDateTime) =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentUUID))
                ?: throw Exception("学生不存在")
            val coach = CoachEntity.findById(UUID.fromString(coachUUID))
                ?: throw Exception("教练不存在")

            if (student.currentCoach >= student.maxCoach) {
                throw Exception("学生已达到最大教练选择数量")
            }

            if (coach.currentStudents >= coach.maxStudents) {
                throw Exception("教练已达到最大学生接收数量")
            }

            // 创建关系
            val relation = StudentCoachRelationEntity.new {
                this.studentID = student
                this.coachID = coach
                this.status = "active"  // 直接使用字符串值
                this.applicationTime = Clock.System.now()
                this.expectedStartTime = Clock.System.now() // 使用当前时间作为预期开始时间
                this.actualStartTime = Clock.System.now()
                this.endTime = null // 不设置结束时间
                this.createdAt = Clock.System.now()
                this.updatedAt = Clock.System.now()
            }

            // 更新计数
            student.currentCoach += 1
            coach.currentStudents += 1

            STUDENT_SERVICE_LOGGER.info("选择教练课程成功")

            // 返回成功信息
            mapOf(
                "success" to true,
                "message" to "学生-教练关系建立成功",
                "studentCurrentCoaches" to student.currentCoach
            )
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
                LOGGER.info("查询校区成功，校区 ID：$campusId，校区名称：${it.campusName}")
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
    fun getRechargeHistoryByusername(username:String, page: Int, size: Int): Pair<List<RechargeRecord>, Long> {
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

            records to totalCount
        }

}