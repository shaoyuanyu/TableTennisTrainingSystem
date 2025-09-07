@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence.user

import io.github.shaoyuanyu.ttts.dto.user.User
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID
import kotlin.time.ExperimentalTime

class UserEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<UserEntity>(UserTable)

    var username by UserTable.username
    var encryptedPassword by UserTable.encrypted_password
    var realName by UserTable.real_name
    var gender by UserTable.gender
    var age by UserTable.age
    var phoneNumber by UserTable.phone_number
    var email by UserTable.email
    var campusId by UserTable.campus_id
    var role by UserTable.role
    var status by UserTable.status
    var createdAt by UserTable.created_at
    var lastLoginAt by UserTable.last_login_at
}

fun UserEntity.expose() = User(
    uuid = id.value.toString(),
    username = username,
    encryptedPassword = encryptedPassword,
    realName = realName,
    gender = gender,
    age = age,
    phoneNumber = phoneNumber,
    email = email,
    campusId = campusId,
    role = role,
    status = status,
    createdAt = createdAt.toLocalDateTime(TimeZone.of("Asia/Shanghai")),
    lastLoginAt = lastLoginAt.toLocalDateTime(TimeZone.of("Asia/Shanghai"))
)

fun UserEntity.exposeWithoutPassword() = User(
    uuid = id.value.toString(),
    username = username,
    realName = realName,
    gender = gender,
    age = age,
    phoneNumber = phoneNumber,
    email = email,
    campusId = campusId,
    role = role,
    status = status,
    createdAt = createdAt.toLocalDateTime(TimeZone.of("Asia/Shanghai")),
    lastLoginAt = lastLoginAt.toLocalDateTime(TimeZone.of("Asia/Shanghai"))
)