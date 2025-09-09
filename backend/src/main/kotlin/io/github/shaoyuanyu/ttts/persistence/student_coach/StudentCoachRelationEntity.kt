@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence.student_coach

import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID
import kotlin.time.ExperimentalTime

class StudentCoachRelationEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<StudentCoachRelationEntity>(StudentCoachRelationTable)

    var coachID by CoachEntity referencedOn StudentCoachRelationTable.coachID
    var studentID by StudentEntity referencedOn StudentCoachRelationTable.studentID
    var status by StudentCoachRelationTable.status
    var startTime by StudentCoachRelationTable.start_time
    var endTime by StudentCoachRelationTable.end_time
    var createdAt by StudentCoachRelationTable.created_at
}