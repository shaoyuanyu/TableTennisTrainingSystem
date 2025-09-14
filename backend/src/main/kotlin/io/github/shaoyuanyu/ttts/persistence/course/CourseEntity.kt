package io.github.shaoyuanyu.ttts.persistence.course

import io.github.shaoyuanyu.ttts.dto.course.Course
import io.github.shaoyuanyu.ttts.persistence.table.TableEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.*

class CourseEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<CourseEntity>(CourseTable)

    var title by CourseTable.title
    var description by CourseTable.description
    var date by CourseTable.date
    var startTime by CourseTable.startTime
    var endTime by CourseTable.endTime
    var location by CourseTable.location
    var status by CourseTable.status
    var price by CourseTable.price
    var coachId by CourseTable.coachId
    var coachName by CourseTable.coachName
    var studentId by CourseTable.studentId
    var studentName by CourseTable.studentName
    var campusId by CourseTable.campusId
    var campusName by CourseTable.campusName
    var tableId by TableEntity referencedOn CourseTable.tableId
    var lessonContent by CourseTable.lessonContent
    var paymentStatus by CourseTable.paymentStatus
    var attendanceStatus by CourseTable.attendanceStatus
    var studentFeedback by CourseTable.studentFeedback
    var studentRating by CourseTable.studentRating
    var createdAt by CourseTable.createdAt
}

fun CourseEntity.expose() = Course(
    id = id.value.toString(),
    title = title,
    description = description,
    date = date.toString(),
    startTime = startTime.toString(),
    endTime = endTime.toString(),
    duration = calculateDuration(startTime, endTime), // 动态计算时长
    location = location,
    status = status.name,
    price = price,
    coachId = coachId.toString(),
    coachName = coachName,
    studentId = studentId.toString(),
    studentName = studentName,
    campusId = campusId,
    campusName = campusName,
    tableId = tableId.toString(),
    tableIndex = tableId.indexInCampus,
    lessonContent = lessonContent,
    paymentStatus = paymentStatus,
    attendanceStatus = attendanceStatus,
    studentFeedback = studentFeedback,
    studentRating = studentRating,
    createdAt = createdAt.toString()
)

/**
 * 计算课程时长（分钟）
 */
private fun calculateDuration(startTime: kotlinx.datetime.LocalTime, endTime: kotlinx.datetime.LocalTime): Int {
    val startSeconds = startTime.toSecondOfDay()
    val endSeconds = endTime.toSecondOfDay()
    return (endSeconds - startSeconds) / 60
}