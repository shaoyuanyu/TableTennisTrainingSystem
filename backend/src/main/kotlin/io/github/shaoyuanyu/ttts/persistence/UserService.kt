@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.user.User
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import io.github.shaoyuanyu.ttts.persistence.user.exposeWithoutPassword
import io.github.shaoyuanyu.ttts.utils.encryptPasswd
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal val LOGGER = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.persistence")

// TODO: 是否使用协程，以否使用 suspend 函数
class UserService(
    private val database: Database
) {
    init {
        // 若 User 表为空（新创建），则自动创建默认 super admin 账户
        // TODO: 提醒 super admin 修改初始密码
        transaction(database) {
            if (UserEntity.all().empty()) {
                UserEntity.new {
                    username = "super_admin"
                    encryptedPassword = encryptPasswd("password")
                    realName = "Super Admin"
                    gender = ""
                    age = 0
                    phoneNumber = ""
                    email = ""
                    campusId = -1
                    role = UserRole.SUPER_ADMIN
                    status = ""
                    createdAt = Clock.System.now()
                    lastLoginAt = createdAt
                }
            }
        }
    }

    /**
     * 创建用户
     */
    fun createUser(newUser: User): String =
        transaction(database) {
            UserEntity.new {
                username = newUser.username
                encryptedPassword = encryptPasswd(newUser.plainPassword!!)
                realName = newUser.realName
                gender = newUser.gender
                age = newUser.age
                phoneNumber = newUser.phoneNumber
                email = newUser.email
                campusId = newUser.campusId
                role = newUser.role
                status = newUser.status
                createdAt = Clock.System.now()
                lastLoginAt = createdAt
            }.id.value.toString()
        }.also { uuid ->
            LOGGER.info("创建用户成功，用户 ID：$uuid，用户名：${newUser.username}")
        }

    /**
     * 根据userId（uuid）查询用户
     */
    fun queryUserByUUID(uuid: String): User =
        transaction(database) {
            UserEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }

                it.exposeWithoutPassword()
            }.also {
                LOGGER.info("查询用户成功，用户 ID：$uuid，用户名：${it.username}")
            }
        }

    /**
     * 根据用户名查询用户
     */
    fun queryUserByUsername(username: String): User =
        transaction(database) {
            UserEntity.find { UserTable.username eq username }.also {
                if (it.empty()) {
                    throw Exception("用户不存在")
                }
            }.first().exposeWithoutPassword().also {
                LOGGER.info("查询用户成功，用户 ID：${it.uuid}, 用户名：${it.username}")
            }
        }

    /**
     * 更新用户信息
     */
    fun updateUser(user: User) {
        transaction(database) {
            UserEntity.findByIdAndUpdate(UUID.fromString(user.uuid)) {
                it.username = user.username
                it.encryptedPassword = encryptPasswd(user.plainPassword!!)
                it.realName = user.realName
                it.gender = user.gender
                it.age = user.age
                it.phoneNumber = user.phoneNumber
                it.email = user.email
                it.campusId = user.campusId
                it.role = user.role
                it.status = user.status
            }.also {
                LOGGER.info("更新用户成功，用户 ID：${user.uuid}, 用户名：${user.username}")
            }
        }
    }

    /**
     * 删除用户
     */
    fun deleteUser(uuid: String) {
        transaction(database) {
            UserEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }

                it.delete()
            }.also {
                LOGGER.info("删除用户成功，用户 ID：$uuid")
            }
        }
    }
}
