package io.github.shaoyuanyu.ttts.persistence.student_course

import io.github.shaoyuanyu.ttts.persistence.course.CourseTable
import io.github.shaoyuanyu.ttts.persistence.student.StudentTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.datetime
import java.util.UUID
import kotlinx.datetime.LocalDateTime

object StudentCourseTable : IntIdTable("student_courses") {
    val studentId: Column<UUID> = uuid("student_id").references(StudentTable.id)
    val courseId: Column<UUID> = uuid("course_id").references(CourseTable.id)
    val enrollmentStatus: Column<String> = varchar("enrollment_status", 32) // enrolled, waitlist, cancelled
    val paymentStatus: Column<String> = varchar("payment_status", 32) // pending, paid, refunded
    val enrolledAt: Column<LocalDateTime> = datetime("enrolled_at")
    val attendanceStatus: Column<String?> = varchar("attendance_status", 32).nullable() // present, absent, late
    val feedback: Column<String?> = text("feedback").nullable()
    val rating: Column<Int?> = integer("rating").nullable() // 1-5

    init {
        index(true, studentId, courseId)
    }
}