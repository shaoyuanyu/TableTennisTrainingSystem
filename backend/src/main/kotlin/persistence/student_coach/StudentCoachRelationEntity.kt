package persistence.student_coach

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class StudentCoachRelationEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<StudentCoachRelationEntity>(StudentCoachRelationTable)

    val coach_username by StudentCoachRelationTable.coach_username
    val student_username by StudentCoachRelationTable.student_username
    val status by StudentCoachRelationTable.status
    val start_time by StudentCoachRelationTable.start_time
    val end_time by StudentCoachRelationTable.end_time
    val created_at by StudentCoachRelationTable.created_at
}