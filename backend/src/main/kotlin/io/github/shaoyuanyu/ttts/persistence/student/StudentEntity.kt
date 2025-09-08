@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.student

import io.github.shaoyuanyu.ttts.dto.student.Student
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID
import kotlin.time.ExperimentalTime

class StudentEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<StudentEntity>(StudentTable)

    var username by StudentTable.username
    var balance by StudentTable.balance
    var maxCoach by StudentTable.max_coach
    var currentCoach by StudentTable.current_coach
    var createdAt by StudentTable.created_at
    var lastLoginAt by StudentTable.last_login_at
}
fun StudentEntity.expose()= Student(
    uuid = id.value.toString(),
    username = username,
    balance = balance,
    maxCoach = maxCoach,
    currentCoach = currentCoach,
    createdAt = createdAt.toLocalDateTime(TimeZone.of("Asia/Shanghai")),
    lastLoginAt = lastLoginAt.toLocalDateTime(TimeZone.of("Asia/Shanghai"))
)
