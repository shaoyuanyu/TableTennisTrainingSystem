@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.student.Student
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.student.expose
import io.github.shaoyuanyu.ttts.persistence.student_coach.StudentCoachRelationEntity
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class StudentService(
    private val database: Database
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
//                LOGGER.info("查询用户成功，用户 ID：$uuid，用户名：${it.username}")
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
                val newbalance=it.balance
                it.balance
            }.also {newbalance->
                LOGGER.info("查询用户余额成功，用户 ID：$uuid，余额：${newbalance}")
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
                val newbalance=it.balance
            }.also {newbalance->
                LOGGER.info("更新用户余额成功，用户 ID：$uuid，余额：${newbalance}")
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
                val newbalance=it.balance
            }.also {newbalance->
                LOGGER.info("充值成功，用户 ID：$uuid，余额：${newbalance}")
            }
        }

    /**
     * 扣费
     */
    fun deduct(uuid: String, amount: Float) =
        transaction(database) {
            StudentEntity.findById(UUID.fromString(uuid)).let { student ->
                if (student == null) {
                    throw Exception("用户不存在")
                }
                if (student.balance < amount) {
                    throw Exception("余额不足")
                }
                student.balance -= amount

                student
            }.also { student ->
                LOGGER.info("扣费成功，用户 ID：$uuid，余额：${student.balance}")
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
//                LOGGER.info("选择教练成功，用户 ID：$uuid")
//            }
//        }
    /**
     * 选择教练课程
     */
    fun selectCourse(studentuuid: String, coachuuid: String, startTime: LocalDateTime,endTime:LocalDateTime) =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentuuid))
                ?: throw Exception("学生不存在")
            val coach = CoachEntity.findById(UUID.fromString(coachuuid))
                ?: throw Exception("教练不存在")

            if (student.currentCoach >= student.maxCoach) {
                throw Exception("学生已达到最大教练选择数量")
            }

            if (coach.currentStudents>= coach.maxStudents) {
                throw Exception("教练已达到最大学生接收数量")
            }

            // 创建关系
            StudentCoachRelationEntity.new {
                studentID = student
                coachID = coach
                status = "active"
                start_time = startTime
                end_time = endTime
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Shanghai"))
            }

            // 更新计数
            student.currentCoach += 1
            coach.currentStudents += 1

            LOGGER.info("选择教练课程成功")

            // 返回成功信息
            mapOf(
                "success" to true,
                "message" to "学生-教练关系建立成功",
                "studentCurrentCoaches" to student.currentCoach
            )
        }
}
