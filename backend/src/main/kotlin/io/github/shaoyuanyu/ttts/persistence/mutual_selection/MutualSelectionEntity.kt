@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence.mutual_selection

import io.github.shaoyuanyu.ttts.dto.mutual_selection.MutualSelection
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID
import kotlin.time.ExperimentalTime

class MutualSelectionEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<MutualSelectionEntity>(MutualSelectionTable)

    var coachID by CoachEntity referencedOn MutualSelectionTable.coach_id

    var studentID by StudentEntity referencedOn MutualSelectionTable.student_id

    // 状态
    var status by MutualSelectionTable.status

    // 申请时间
    var applicationTime by MutualSelectionTable.application_time

    // 预期开始时间
    var expectedStartTime by MutualSelectionTable.expected_start_time

    // 实际开始时间
    var actualStartTime by MutualSelectionTable.actual_start_time

    // 结束时间
    var endTime by MutualSelectionTable.end_time
}

fun MutualSelectionEntity.expose() = MutualSelection(
    selectionId = id.value.toString(),
    coachId = coachID.id.value.toString(),
    coachName = coachID.userId.realName,
    studentId = studentID.id.value.toString(),
    studentName = studentID.userId.realName,
    status = status,
    applicationTime = applicationTime.toLocalDateTime(TimeZone.of("Asia/Shanghai")),
    expectedStartTime = expectedStartTime.toLocalDateTime(TimeZone.of("Asia/Shanghai")),
)

fun List<MutualSelectionEntity>.expose() = map { it.expose() }