@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.student_coach.StudentCoachRelationEntity
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal val STUDENT_SERVICE_LOGGER: Logger = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.persistence.student")

class StudentService(
    private val database: Database,
    private val userService: UserService
) {
    init {
        transaction(database) {

        }
    }

//    /**
//     * query student by uuid
//     */
//    fun queryStudentByUUID(uuid: String): Student =
//        transaction(database) {
//            StudentEntity.findById(UUID.fromString(uuid)).let {
//                if (it == null) {
//                    throw Exception("用户不存在")
//                }
//
//                it.expose()
//            }.also {
//                STUDENT_SERVICE_LOGGER.info("查询用户成功，用户 ID：$uuid，用户名：${it.username}")
//            }
//        }

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
                STUDENT_SERVICE_LOGGER.info("查询用户余额成功，用户 ID：$uuid，余额：${newBalance}")
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
                STUDENT_SERVICE_LOGGER.info("更新用户余额成功，用户 ID：$uuid，余额：${newBalance}")
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
            }.also { newBalance ->
                STUDENT_SERVICE_LOGGER.info("充值成功，用户 ID：$uuid，余额：${newBalance}")
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
                STUDENT_SERVICE_LOGGER.info("扣费成功，用户 ID：$uuid，余额：${student}")
            }
        }
//
//    /**
//     * 选择教练前判断
//     */
//    fun judgeCoachNumber(uuid: String): Boolean =
//        transaction(database) {
//            StudentEntity.findById(UUID.fromString(uuid)).let {
//                if (it == null) {
//                    throw Exception("用户不存在")
//                }
//                if(it.currentCoach>=it.maxCoach){
//                    throw Exception("已达最大可选教练数，无法选择更多教练")
//                }
//                it.currentCoach+=1
//                true
//            }.also {
//                STUDENT_SERVICE_LOGGER.info("选择教练成功，用户 ID：$uuid")
//            }
//        }
    /**
     * 选择教练课程
     */
    fun selectCourse(studentUUID: String, coachUUID: String, startTime: Instant, endTime:Instant) =
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
            // 获取教练所属的校区
            val campusID =userService.queryUserByUUID(coachUUID).campusId

            //根据campusID获取校区实体
            val campus = queryCampusById(campusID)

            // 计算上课时间（小时）
            val duration: Duration = endTime - startTime
            val durationHours = duration.inWholeHours.toFloat()

            if (durationHours <= 0) {
                throw Exception("课程时间无效，结束时间必须晚于开始时间")
            }

            // 根据教练等级计算费用
            val hourlyRate = when (val coachLevel = coach.level) {
                "初级" -> 80.0f
                "中级" -> 150.0f
                "高级" -> 200.0f
                else -> throw Exception("未知的教练等级: $coachLevel")
            }

            val totalCost = hourlyRate * durationHours

            // 调用扣费函数
            deductBalance(studentUUID, totalCost)

            // 为教练增加余额（扣费金额的60%）
            val coachEarnings = totalCost * 0.6f
            coach.balance += coachEarnings

            //校区增加金额（扣费金额的40%）
            val campusEarnings = totalCost * 0.4f
            campus.balance += campusEarnings

            // 创建关系
            StudentCoachRelationEntity.new {
                studentID = student
                coachID = coach
                status = "active"
                this.startTime = startTime
                this.endTime = endTime
                createdAt = Clock.System.now()
            }

            // 更新计数
            student.currentCoach += 1
            coach.currentStudents += 1

            STUDENT_SERVICE_LOGGER.info("选择教练课程成功，课程时长: ${durationHours}小时，总费用: ${totalCost}元")

            // 返回成功信息
            mapOf(
                "success" to true,
                "message" to "学生-教练关系建立成功",
                "studentCurrentCoaches" to student.currentCoach,
                "durationHours" to durationHours,
                "totalCost" to totalCost,
                "coachEarnings" to coachEarnings
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
                STUDENT_SERVICE_LOGGER.info("查询校区成功，校区 ID：$campusId，校区名称：${it.campus_name}")
            }
        }

}