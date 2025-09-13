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

    var coachID by CoachEntity referencedOn StudentCoachRelationTable.coach_id
    var studentID by StudentEntity referencedOn StudentCoachRelationTable.student_id
    // 状态
    var status by StudentCoachRelationTable.status

    // 申请时间
    var applicationTime by StudentCoachRelationTable.application_time

    // 预期开始时间
    var expectedStartTime by StudentCoachRelationTable.expected_start_time

    // 实际开始时间
    var actualStartTime by StudentCoachRelationTable.actual_start_time

    // 结束时间
    var endTime by StudentCoachRelationTable.end_time

    // 创建时间
    var createdAt by StudentCoachRelationTable.created_at

    // 更新时间
    var updatedAt by StudentCoachRelationTable.updated_at

    // 学生留言
    var studentMessage by StudentCoachRelationTable.student_message

    // 教练回复
    var coachMessage by StudentCoachRelationTable.coach_message
}