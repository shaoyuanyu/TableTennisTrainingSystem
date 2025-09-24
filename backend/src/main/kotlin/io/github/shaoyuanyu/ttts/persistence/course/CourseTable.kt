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

object CourseTable : UUIDTable("courses") {
    val coach = reference("coach", CoachTable, ReferenceOption.CASCADE)

    val student = reference("student", StudentTable, ReferenceOption.CASCADE)

    val campus = reference("campus", CampusTable, ReferenceOption.CASCADE)

    // 球桌
    val table = reference("table", TableTable, ReferenceOption.CASCADE)

    val title: Column<String> = varchar("title", 255)

    val date: Column<LocalDate> = date("date")

    val startTime: Column<LocalTime> = time("start_time")

    val endTime: Column<LocalTime> = time("end_time")

    val status: Column<CourseStatus> = customEnumeration(
        name = "status",
        sql = "ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED')",
        fromDb = { value -> CourseStatus.valueOf(value as String) },
        toDb = { it.name }
    )

    val price: Column<Float> = float("price")

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