@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.student.Student
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.student.expose
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

    /** Create student
     */
    fun createStudent(newStudent: Student): String =
        transaction(database) {
            StudentEntity.new {
                username = newStudent.username
                balance = 0.0f
                createdAt = Clock.System.now()
                lastLoginAt = this.createdAt
            }.id.value.toString()
        }.also { uuid ->
            LOGGER.info("创建用户成功，用户 ID：$uuid，用户名：${newStudent.username}")
        }

    /**
     * query student by uuid
     */
    fun queryStudentByUUID(uuid: String): Student =
        transaction(database) {
            StudentEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }

                it.expose()
            }.also {
                LOGGER.info("查询用户成功，用户 ID：$uuid，用户名：${it.username}")
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

    /**
     * 删除用户
     */
    fun deleteStudent(uuid: String) {
        transaction(database) {
            StudentEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }
                if(it.balance!=0.0f){
                    throw Exception("用户余额不为0，无法删除")
                }
                it.delete()
            }.also {
                LOGGER.info("删除用户成功，用户 ID：$uuid")
            }
        }
    }
    /**
     * 选择教练前判断
     */
    fun judgeCoachNumber(uuid: String): Boolean =
        transaction(database) {
            StudentEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }
                if(it.currentCoach>=it.maxCoach){
                    throw Exception("已达最大可选教练数，无法选择更多教练")
                }
                it.currentCoach+=1
                true
            }.also {
                LOGGER.info("选择教练成功，用户 ID：$uuid")
            }
        }
}

