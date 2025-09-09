package io.github.shaoyuanyu.ttts.persistence.student_coach

import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.coach.CoachTable
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.student.StudentTable
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class StudentCoachRelationEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<StudentCoachRelationEntity>(StudentCoachRelationTable)

    var coachID by CoachEntity referencedOn StudentCoachRelationTable.coachID
//    var userId by UserEntity referencedOn StudentTable.id
    var studentID by StudentEntity referencedOn StudentCoachRelationTable.studentID
//    var studentID by StudentCoachRelationTable.student_username
    var status by StudentCoachRelationTable.status
    var start_time by StudentCoachRelationTable.start_time
    var end_time by StudentCoachRelationTable.end_time
    var createdAt by StudentCoachRelationTable.created_at
}