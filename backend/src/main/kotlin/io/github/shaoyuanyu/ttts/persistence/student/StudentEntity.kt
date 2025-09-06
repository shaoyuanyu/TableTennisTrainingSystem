package io.github.shaoyuanyu.ttts.persistence.student

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID
class StudentEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<StudentEntity>(StudentTable)

    val username by StudentTable.username
    val balance by StudentTable.balance
    val created_at by StudentTable.created_at
    var last_login_at by StudentTable.last_login_at
}