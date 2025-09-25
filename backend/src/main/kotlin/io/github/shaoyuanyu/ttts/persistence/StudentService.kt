@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.recharge.RechargeRecord
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import io.github.shaoyuanyu.ttts.persistence.recharge.RechargeEntity
import io.github.shaoyuanyu.ttts.persistence.recharge.RechargeTable
import io.github.shaoyuanyu.ttts.persistence.recharge.expose
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
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
}