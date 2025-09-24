package io.github.shaoyuanyu.ttts.persistence.course

import io.github.shaoyuanyu.ttts.dto.course.Course
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.table.TableEntity
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.*

class CourseEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<CourseEntity>(CourseTable)

    var coach by CoachEntity referencedOn CourseTable.coach

    var student by StudentEntity referencedOn CourseTable.student

    var campus by CampusEntity referencedOn CourseTable.campus

    var table by TableEntity referencedOn CourseTable.table

    var title by CourseTable.title

    var date by CourseTable.date

    var startTime by CourseTable.startTime

    var endTime by CourseTable.endTime

    var status by CourseTable.status

    var price: Float by CourseTable.price

    var paymentStatus by CourseTable.paymentStatus

    var attendanceStatus by CourseTable.attendanceStatus

    var studentFeedback by CourseTable.studentFeedback

    var studentRating by CourseTable.studentRating

    var createdAt by CourseTable.createdAt
}

fun CourseEntity.expose() = Course(
    id = id.value.toString(),
    coachId = coach.id.value.toString(),
    coachName = coach.userId.realName,
    studentId = student.id.value.toString(),
    studentName = student.userId.realName,
    campusId = campus.id.value,
    campusName = campus.campusName,
    tableId = table.id.value.toString(),
    tableIndex = table.indexInCampus,
    title = title,
    date = date.toString(),
    startTime = startTime.toString(),
    endTime = endTime.toString(),
    duration = calculateDuration(startTime, endTime), // 动态计算时长
    status = status.name,
    price = price,
    paymentStatus = paymentStatus,
    attendanceStatus = attendanceStatus,
    studentFeedback = studentFeedback,
    studentRating = studentRating,
    createdAt = createdAt.toString(),
)

/**
 * 计算课程时长（分钟）
 */
private fun calculateDuration(startTime: LocalTime, endTime: LocalTime): Int {
    val startSeconds = startTime.toSecondOfDay()
    val endSeconds = endTime.toSecondOfDay()
    return (endSeconds - startSeconds) / 60
}