package io.github.shaoyuanyu.ttts.persistence.user

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class UserEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<UserEntity>(UserTable)

    var username by UserTable.username
    var encrypted_password by UserTable.encrypted_password
    var real_name by UserTable.real_name
    var gender by UserTable.gender
    var age by UserTable.age
    var phone_number by UserTable.phone_number
    var email by UserTable.email
    var campus_id by UserTable.campus_id
    var status by UserTable.status
    var created_at by UserTable.created_at
    var last_login_at by UserTable.last_login_at
}