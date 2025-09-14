package io.github.shaoyuanyu.ttts.persistence.course

import io.github.shaoyuanyu.ttts.persistence.campus.CampusTable
import io.github.shaoyuanyu.ttts.persistence.coach.CoachTable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.datetime
import org.jetbrains.exposed.v1.datetime.time
import java.util.*

object CourseTable : UUIDTable("courses") {
    val title: Column<String> = varchar("title", 255)
    val description: Column<String?> = text("description").nullable()
    val type: Column<String> = varchar("type", 32) // individual or group
    val level: Column<String> = varchar("level", 32) // beginner, intermediate, advanced
    val date: Column<LocalDate> = date("date")
    val startTime: Column<LocalTime> = time("start_time")
    val endTime: Column<LocalTime> = time("end_time")
    val duration: Column<Int> = integer("duration") // minutes
    val location: Column<String> = varchar("location", 255)
    val status: Column<String> = varchar("status", 32) // scheduled, confirmed, completed, cancelled
    val maxStudents: Column<Int?> = integer("max_students").nullable()
    val currentStudents: Column<Int> = integer("current_students").default(0)
    val price: Column<Int> = integer("price") // cents
    val coachId: Column<UUID> = uuid("coach_id").references(CoachTable.id)
    val coachName: Column<String> = varchar("coach_name", 255)
    val campusId: Column<Int> = integer("campus_id").references(CampusTable.id)
    val campusName: Column<String> = varchar("campus_name", 255)
    val createdAt: Column<LocalDateTime> = datetime("created_at")
    val updatedAt: Column<LocalDateTime> = datetime("updated_at")
}