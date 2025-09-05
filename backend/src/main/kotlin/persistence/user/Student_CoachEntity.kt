package persistence.user

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class Student_CoachEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<Student_CoachEntity>(Student_CoachTable)

    val coach_username by Student_CoachTable.coach_username
    val student_username by Student_CoachTable.student_username
    val status by Student_CoachTable.status
    val start_time by Student_CoachTable.start_time
    val end_time by Student_CoachTable.end_time
    val created_at by Student_CoachTable.created_at
}