package io.github.shaoyuanyu.ttts.persistence.student_coach

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class StudentCoachRelationEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<StudentCoachRelationEntity>(StudentCoachRelationTable)

    var coach_username by StudentCoachRelationTable.coach_username
    var student_username by StudentCoachRelationTable.student_username
    var status by StudentCoachRelationTable.status
    var start_time by StudentCoachRelationTable.start_time
    var end_time by StudentCoachRelationTable.end_time
    var createdAt by StudentCoachRelationTable.created_at
}