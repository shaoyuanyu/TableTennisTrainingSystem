@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.coach.CoachInfo
import io.github.shaoyuanyu.ttts.dto.student.StudentInfo
import io.github.shaoyuanyu.ttts.dto.user.User
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import io.github.shaoyuanyu.ttts.persistence.user.expose
import io.github.shaoyuanyu.ttts.persistence.user.exposeWithoutPassword
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.utils.encryptPasswd
import io.github.shaoyuanyu.ttts.utils.validatePasswd
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
     * 检查用户名是否存在
     *
     * 该函数用于检查指定用户名是否已存在于数据库中。
     *
     * @param username 要检查的用户名
     * @param excludeUuid 可选参数，排除指定UUID的用户（用于更新时检查）
     * @return 如果用户名已存在返回true，否则返回false
     */
    fun isUsernameExists(username: String, excludeUuid: String? = null): Boolean =
        transaction(database) {
            val query = UserEntity.find { UserTable.username eq username }
            val filteredQuery = excludeUuid?.let { 
                query.filter { userEntity -> userEntity.id.value != UUID.fromString(it) }
            } ?: query
            filteredQuery.count() > 0
        }

    /**
     * 创建用户
     */
    fun createUser(newUser: User): String =
        transaction(database) {
            // 检查用户名是否已存在
            if (isUsernameExists(newUser.username)) {
                throw IllegalArgumentException("用户名已存在")
            }
            
            // 提前验证角色特定信息
            when (newUser.role) {
                UserRole.STUDENT -> {
                    if (newUser.studentInfo == null) {
                        throw IllegalArgumentException("学生用户必须提供学生信息")
                    }
                }
                
                UserRole.COACH -> {
                    if (newUser.coachInfo == null) {
                        throw IllegalArgumentException("教练用户必须提供教练信息")
                    }
                }
                
                else -> {
                    // 其他角色不需要额外验证
                }
            }
            
            // 创建用户账户
            val userEntity = UserEntity.new {
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
            }
            val userId = userEntity.id.value.toString()
            
            // 根据用户角色创建相应的角色特定记录
            when (newUser.role) {
                UserRole.STUDENT -> {
                    val studentInfo = newUser.studentInfo!!
                    StudentEntity.new(UUID.fromString(userId)) {
                        this.userId = userEntity
                        this.balance = studentInfo.balance
                        this.maxCoach = studentInfo.maxCoach
                        this.currentCoach = 0
                    }
                }
                
                UserRole.COACH -> {
                    val coachInfo = newUser.coachInfo!!
                    CoachEntity.new(UUID.fromString(userId)) {
                        this.userId = userEntity
                        this.photoUrl = coachInfo.photoUrl ?: ""
                        this.achievements = coachInfo.achievements ?: ""
                        this.level = coachInfo.level ?: ""
                        this.hourlyRate = coachInfo.hourlyRate
                        this.balance = 0.0f
                        this.maxStudents = 20
                        this.currentStudents = 0
                        this.isApproved = false
                        this.approvedBy = -1
                    }
                }
                
                else -> {
                    // 其他角色不需要创建额外记录
                }
            }
            
            userId
        }.also { uuid ->
            LOGGER.info("创建用户成功，用户 ID：$uuid，用户名：${newUser.username}")
        }

    /**
     * 根据userId（uuid）查询用户
     */
    fun queryUserByUUID(uuid: String): User =
        transaction(database) {
            UserEntity.findById(UUID.fromString(uuid)).let { userEntity ->
                if (userEntity == null) {
                    throw Exception("用户不存在")
                }

                // 根据用户角色附加角色特定信息
                val user = userEntity.exposeWithoutPassword()
                when (user.role) {
                    UserRole.STUDENT -> {
                        val studentEntity = StudentEntity.findById(UUID.fromString(uuid))
                        if (studentEntity != null) {
                            user.copy(
                                studentInfo = StudentInfo(
                                    balance = studentEntity.balance,
                                    maxCoach = studentEntity.maxCoach,
                                    currentCoach = studentEntity.currentCoach
                                )
                            )
                        } else {
                            user
                        }
                    }
                    UserRole.COACH -> {
                        val coachEntity = CoachEntity.findById(UUID.fromString(uuid))
                        if (coachEntity != null) {
                            user.copy(
                                coachInfo = CoachInfo(
                                    photoUrl = coachEntity.photoUrl,
                                    achievements = coachEntity.achievements,
                                    level = coachEntity.level,
                                    hourlyRate = coachEntity.hourlyRate,
                                    balance = coachEntity.balance,
                                    maxStudents = coachEntity.maxStudents,
                                    currentStudents = coachEntity.currentStudents,
                                    isApproved = coachEntity.isApproved,
                                    approvedBy = coachEntity.approvedBy
                                )
                            )
                        } else {
                            user
                        }
                    }
                    else -> user
                }
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
     * 更新用户信息和专有信息
     *
     * 该函数用于更新用户的基本信息以及学生或教练的专有信息。
     * 根据用户角色的不同，会同时更新相应的角色特定表中的数据。
     *
     * @param user 包含更新信息的User对象
     * @throws IllegalArgumentException 当用户名已存在时抛出异常
     */
    fun updateUser(user: User) {
        transaction(database) {
            // 检查用户名是否已存在（排除当前用户）
            if (isUsernameExists(user.username, user.uuid)) {
                throw IllegalArgumentException("用户名已存在")
            }
            
            // 更新用户基本信息
            UserEntity.findByIdAndUpdate(UUID.fromString(user.uuid)) {
                it.username = user.username
                it.realName = user.realName
                it.gender = user.gender
                it.age = user.age
                it.phoneNumber = user.phoneNumber
                it.email = user.email
                it.campusId = user.campusId
                // 注意：不更新角色和状态字段，防止用户越权修改
            }
            
            // 根据用户角色更新专有信息
            when (user.role) {
                UserRole.STUDENT -> {
                    user.studentInfo?.let { studentInfo ->
                        StudentEntity.findByIdAndUpdate(UUID.fromString(user.uuid)) {
                            it.balance = studentInfo.balance
                            it.maxCoach = studentInfo.maxCoach
                        }
                    }
                }
                UserRole.COACH -> {
                    user.coachInfo?.let { coachInfo ->
                        CoachEntity.findByIdAndUpdate(UUID.fromString(user.uuid)) {
                            it.photoUrl = coachInfo.photoUrl ?: ""
                            it.achievements = coachInfo.achievements ?: ""
                            it.level = coachInfo.level ?: ""
                            it.hourlyRate = coachInfo.hourlyRate
                            it.maxStudents = coachInfo.maxStudents
                        }
                    }
                }
                else -> {
                    // 其他角色没有专有信息需要更新
                }
            }
        }.also {
            LOGGER.info("更新用户信息成功，用户 ID：${user.uuid}, 用户名：${user.username}")
        }
    }

    /**
     * 验证用户身份
     *
     * 该函数通过用户名查找用户，并验证提供的明文密码是否与存储的加密密码匹配。
     * 如果验证成功，将记录登录信息并返回用户ID；否则返回null。
     *
     * @param username 用户名
     * @param plainPassword 明文密码
     * @return 用户UUID字符串，如果验证失败则返回null，如果用户不存在则抛出异常
     * @throws Exception 当用户不存在时抛出异常
     */
    fun validateUser(username: String, plainPassword: String): String =
        transaction(database) {
            UserEntity.find {
                UserTable.username eq username
            }.also {
                if (it.empty()) {
                    throw UnauthorizedException("用户名不存在")
                }
            }.first().expose().let { user ->
                if (validatePasswd(plainPassword, user.encryptedPassword!!)) {
                    LOGGER.info("验证用户成功，用户 ID：${user.uuid}, 用户名：${user.username}")

                    user.uuid.toString()
                } else {
                    throw UnauthorizedException("密码错误")
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

    /**
     * 更新用户密码
     */
    fun updateUserPassword(uuid: String, oldPassword: String, newPassword: String) {
        transaction(database) {
            UserEntity.findById(UUID.fromString(uuid)).let {
                if (it == null) {
                    throw Exception("用户不存在")
                }
                if(!validatePasswd(oldPassword, it.encryptedPassword)){
                    throw Exception("原密码错误")
                }

                it.encryptedPassword = encryptPasswd(newPassword)
            }.also {
                LOGGER.info("更新用户密码成功，用户 ID：$uuid")
            }
        }
    }
}