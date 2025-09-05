package persistence.user

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.datetime.datetime

object Student_CoachTable : UUIDTable("student_coach") {
    // TODO: 使用 foreign key
    val coach_username: Column<String> = varchar("cooach_username", 64).uniqueIndex()
    // TODO: 使用 foreign key
    val student_username: Column<String> = varchar("student_username", 64).uniqueIndex()
    // TODO: 使用 enum
    val status: Column<String> = varchar("status", 32)

    val start_time: Column<LocalDateTime> = datetime("start_time")

    val end_time: Column<LocalDateTime> = datetime("end_time")

    val created_at: Column<LocalDateTime> = datetime("created_at")

}