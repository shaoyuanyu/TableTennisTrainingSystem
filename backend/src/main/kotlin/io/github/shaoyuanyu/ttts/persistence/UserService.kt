@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.user.User
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.injectCoachEntity
import io.github.shaoyuanyu.ttts.dto.user.injectStudentEntity
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.UnauthorizedException
import io.github.shaoyuanyu.ttts.exceptions.NotFoundException
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import io.github.shaoyuanyu.ttts.persistence.user.expose
import io.github.shaoyuanyu.ttts.persistence.user.exposeWithoutPassword
import io.github.shaoyuanyu.ttts.utils.encryptPasswd
import io.github.shaoyuanyu.ttts.utils.validatePasswd
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal val USER_LOGGER = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.persistence.user")

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
                    campusId = 1
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
                throw BadRequestException("用户名已存在")
            }

            // 检查手机号是否已存在
            if (newUser.phoneNumber.isNotBlank()) {
                val existingPhoneUser = UserEntity.find { UserTable.phone_number eq newUser.phoneNumber }.firstOrNull()
                if (existingPhoneUser != null) {
                    throw BadRequestException("手机号已被注册")
                }
            }

            // 检查邮箱是否已存在
            if (newUser.email.isNotBlank()) {
                val existingEmailUser = UserEntity.find { UserTable.email eq newUser.email}.firstOrNull()
                if (existingEmailUser != null) {
                    throw BadRequestException("邮箱已被注册")
                }
            }
            if (newUser.username.isBlank()) {
                throw BadRequestException("用户名不能为空")
            }
            if (newUser.plainPassword.isNullOrBlank()) {
                throw BadRequestException("密码不能为空")
            }
            if (newUser.realName.isBlank()) {
                throw BadRequestException("真实姓名不能为空")
            }
            // 验证字段格式
            if (!newUser.username.matches(Regex("^[a-zA-Z0-9_]{4,20}$"))) {
                throw BadRequestException("用户名必须是4-20位的字母、数字或下划线")
            }

            if (!newUser.plainPassword.matches(Regex("^(?=.*[A-Za-z])(?=.*\\d).{8,}$"))) {
                throw BadRequestException("密码必须至少8位，包含字母和数字")
            }

            if ( !newUser.phoneNumber.matches(Regex("^1[3-9]\\d{9}$"))) {
                throw BadRequestException("手机号格式不正确")
            }

            if (!newUser.email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"))) {
                throw BadRequestException("邮箱格式不正确")
            }

            if (newUser.age < 1 || newUser.age > 120) {
                throw BadRequestException("年龄必须在1-120之间")
            }


            // 提前验证角色特定信息
            when (newUser.role) {
                UserRole.STUDENT -> {
                    if (newUser.studentInfo == null) {
                        throw BadRequestException("学生用户必须提供学生信息")
                    }
                    // 验证学生信息
                    if (newUser.studentInfo.balance < 0) {
                        throw BadRequestException("学生余额不能为负数")
                    }
                }

                UserRole.COACH -> {
                    if (newUser.coachInfo == null) {
                        throw BadRequestException("教练用户必须提供教练信息")
                    }
                    // 验证教练信息
                    if (!newUser.coachInfo.photoUrl.isNullOrBlank() && !newUser.coachInfo.photoUrl.matches(Regex("^https?://.+"))) {
                        throw BadRequestException("教练照片URL格式不正确")
                    }
                }

                else -> {
                    // 其他角色不需要额外验证
                }
            }

            // 创建用户账户
            val userEntity = UserEntity.new {
                username = newUser.username
                encryptedPassword = encryptPasswd(newUser.plainPassword)
                realName = newUser.realName
                gender = newUser.gender
                age = newUser.age
                phoneNumber = newUser.phoneNumber
                email = newUser.email
                campusId = newUser.campusId
                role = newUser.role
                status = "ACTIVE"
                createdAt = Clock.System.now()
                lastLoginAt = createdAt
            }
            val userId = userEntity.id.value.toString()
            
            // 根据用户角色创建相应的角色特定记录
            when (newUser.role) {
                UserRole.STUDENT -> {
                    //val studentInfo =
                    newUser.studentInfo!!
                    StudentEntity.new(UUID.fromString(userId)) {
                        this.userId = userEntity
                        this.balance = 0.0f
                        this.maxCoach = 2
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
                        this.approvedBy = ""
                    }
                }
                
                else -> {
                    // 其他角色不需要创建额外记录
                }
            }
            
            userId
        }.also { uuid ->
            USER_LOGGER.info("创建用户成功，用户 ID：$uuid，用户名：${newUser.username}")
        }

    /**
     * 根据userId（uuid）查询用户
     */
    fun queryUserByUUID(uuid: String): User =
        transaction(database) {
            UserEntity.findById(UUID.fromString(uuid)).let { userEntity ->
                if (userEntity == null) {
                    throw NotFoundException("用户不存在")
                }

                // 根据用户角色附加角色特定信息
                val user = userEntity.exposeWithoutPassword()
                when (user.role) {
                    UserRole.STUDENT -> {
                        val studentEntity = StudentEntity.findById(UUID.fromString(uuid))
                        if (studentEntity != null) {
                            user.injectStudentEntity(studentEntity)
                        } else {
                            user
                        }
                    }
                    UserRole.COACH -> {
                        val coachEntity = CoachEntity.findById(UUID.fromString(uuid))
                        if (coachEntity != null) {
                            user.injectCoachEntity(coachEntity)
                        } else {
                            user
                        }
                    }
                    else -> user
                }
            }.also {
                USER_LOGGER.info("查询用户成功，用户 ID：$uuid，用户名：${it.username}")
            }
        }

    /**
     * 根据用户名查询用户
     */
    fun queryUserByUsername(username: String): User =
        transaction(database) {
            UserEntity.find { UserTable.username eq username }.also {
                if (it.empty()) {
                    throw NotFoundException("用户不存在")
                }
            }.first().exposeWithoutPassword().also {
                USER_LOGGER.info("查询用户成功，用户 ID：${it.uuid}, 用户名：${it.username}")
            }
        }

    /**
     * 查询用户列表
     *
     * @param page 页码，从1开始
     * @param size 每页大小
     * @param role 用户角色过滤
     * @param campusId 校区ID过滤
     * @return 用户列表和总数
     */
    fun queryUsers(
        page: Int = 1,
        size: Int = 10,
        role: UserRole? = null,
        campusId: Int? = null
    ): Pair<List<User>, Long> = transaction(database) {
        var query = UserEntity.all().toList()
        
        // 根据角色筛选
        role?.let {
            query = query.filter { user -> user.role == it }
        }
        
        // 根据校区ID筛选
        campusId?.let {
            query = query.filter { user -> user.campusId == it }
        }
        
        // 获取总数
        val totalCount = query.size.toLong()
        
        // 分页查询
        val users = query
            .drop((page - 1) * size)
            .take(size)
            .map { userEntity ->
                // 根据用户角色附加角色特定信息
                val user = userEntity.exposeWithoutPassword()
                when (user.role) {
                    UserRole.STUDENT -> {
                        val studentEntity = StudentEntity.findById(UUID.fromString(user.uuid))
                        if (studentEntity != null) {
                            user.injectStudentEntity(studentEntity)
                        } else {
                            user
                        }
                    }
                    UserRole.COACH -> {
                        val coachEntity = CoachEntity.findById(UUID.fromString(user.uuid))
                        if (coachEntity != null) {
                            user.injectCoachEntity(coachEntity)
                        } else {
                            user
                        }
                    }
                    else -> user
                }
            }
        
        users to totalCount
    }.also {
        USER_LOGGER.info("查询用户列表成功，页码：$page，每页数量：$size，角色：$role，校区ID：$campusId")
    }

    /**
     * 更新用户信息和专有信息
     *
     * 该函数用于更新用户的基本信息以及学生或教练的专有信息。
     * 根据用户角色的不同，会同时更新相应的角色特定表中的数据。
     *
     * @param user 包含更新信息的User对象
     * @throws BadRequestException 当用户名已存在时抛出异常
     */
    fun updateUser(user: User) {
        transaction(database) {
            // 检查用户名是否已存在（排除当前用户）
            if (isUsernameExists(user.username, user.uuid)) {
                throw BadRequestException("用户名已存在")
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
            USER_LOGGER.info("更新用户信息成功，用户 ID：${user.uuid}, 用户名：${user.username}")
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
     * @throws BadRequestException 当用户不存在时抛出异常
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
                    USER_LOGGER.info("验证用户成功，用户 ID：${user.uuid}, 用户名：${user.username}")

                    user.uuid.toString()
                } else {
                    throw UnauthorizedException("密码错误")
                }
            }
        }

    /**
     * 修改用户密码
     *
     * 该函数用于用户修改自己的密码，需要提供旧密码进行验证
     *
     * @param uuid 用户UUID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws BadRequestException 当用户不存在或旧密码错误时抛出异常
     */
    fun changeUserPassword(uuid: String, oldPassword: String, newPassword: String) {
        transaction(database) {
            UserEntity.findById(UUID.fromString(uuid)).let { userEntity ->
                if (userEntity == null) {
                    throw NotFoundException("用户不存在")
                }
                
                // 验证旧密码
                if (!validatePasswd(oldPassword, userEntity.encryptedPassword)) {
                    throw BadRequestException("旧密码错误")
                }
                
                // 更新为新密码
                userEntity.encryptedPassword = encryptPasswd(newPassword)
            }.also {
                USER_LOGGER.info("修改用户密码成功，用户 ID：$uuid")
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
                    throw NotFoundException("用户不存在")
                }

                it.delete()
            }.also {
                USER_LOGGER.info("删除用户成功，用户 ID：$uuid")
            }
        }
    }
}