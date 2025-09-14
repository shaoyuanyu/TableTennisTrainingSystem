package io.github.shaoyuanyu.ttts.persistence.student_course

import io.github.shaoyuanyu.ttts.dto.course.StudentCourse
import io.github.shaoyuanyu.ttts.persistence.course.CourseEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class StudentCourseEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StudentCourseEntity>(StudentCourseTable)

    var studentId by UserEntity referencedOn StudentCourseTable.studentId
    var courseId by CourseEntity referencedOn StudentCourseTable.courseId
    var enrollmentStatus by StudentCourseTable.enrollmentStatus
    var paymentStatus by StudentCourseTable.paymentStatus
    var enrolledAt by StudentCourseTable.enrolledAt
    var attendanceStatus by StudentCourseTable.attendanceStatus
    var feedback by StudentCourseTable.feedback
    var rating by StudentCourseTable.rating
}

fun StudentCourseEntity.expose() = StudentCourse(
    studentId = studentId.id.value.toString(),
    courseId = courseId.id.value.toString(),
    courseTitle = courseId.title,
    courseDescription = courseId.description,
    courseType = courseId.type,
    courseLevel = courseId.level,
    date = courseId.date.toString(),
    startTime = courseId.startTime.toString(),
    endTime = courseId.endTime.toString(),
    location = courseId.location,
    enrollmentStatus = this.enrollmentStatus,
    paymentStatus = this.paymentStatus,
    attendanceStatus = this.attendanceStatus,
    feedback = this.feedback,
    rating = this.rating,
    coachName = courseId.coachName,
    campusName = courseId.campusName,
    enrolledAt = this.enrolledAt.toString()
)