package io.github.shaoyuanyu.ttts.persistence.course

import io.github.shaoyuanyu.ttts.dto.course.CourseStatus
import io.github.shaoyuanyu.ttts.persistence.campus.CampusTable
import io.github.shaoyuanyu.ttts.persistence.coach.CoachTable
import io.github.shaoyuanyu.ttts.persistence.student.StudentTable
import io.github.shaoyuanyu.ttts.persistence.table.TableTable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.datetime.time
import java.util.*

object CourseTable : UUIDTable("courses") {
    val title: Column<String> = varchar("title", 255)

    val description: Column<String?> = text("description").nullable() // 课程简介（可选）

    val date: Column<LocalDate> = date("date")

    val startTime: Column<LocalTime> = time("start_time")

    val endTime: Column<LocalTime> = time("end_time")

    val location: Column<String> = varchar("location", 255)

    val status: Column<CourseStatus> = customEnumeration(
        name = "status",
        sql = "ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED')",
        fromDb = { value -> CourseStatus.valueOf(value as String) },
        toDb = { it.name }
    )

    val price: Column<Int> = integer("price")

    val coachId: Column<UUID> = uuid("coach_id").references(CoachTable.id)

    val coachName: Column<String> = varchar("coach_name", 255)

    val studentId: Column<UUID> = uuid("student_id").references(StudentTable.id)

    val studentName: Column<String> = varchar("student_name", 255)

    val campusId: Column<Int> = integer("campus_id").references(CampusTable.id)

    val campusName: Column<String> = varchar("campus_name", 255)

    // 球桌
    val tableId = reference("table_id", TableTable, ReferenceOption.CASCADE)

    // 教练课后填写的课程内容
    val lessonContent: Column<String?> = text("lesson_content").nullable()

    // PENDING, PAID, REFUNDED
    val paymentStatus: Column<String> = varchar("payment_status", 32).default("PENDING")

    // PRESENT, ABSENT, LATE
    val attendanceStatus: Column<String?> = varchar("attendance_status", 32).nullable()

    // 学生对课程的反馈
    val studentFeedback: Column<String?> = text("student_feedback").nullable()

    // 学生评分 1-5
    val studentRating: Column<Int?> = integer("student_rating").nullable()

    val createdAt: Column<LocalDateTime> = datetime("created_at")
}