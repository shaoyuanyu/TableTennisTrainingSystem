package io.github.shaoyuanyu.ttts.persistence.student_coach

import io.github.shaoyuanyu.ttts.persistence.coach.CoachTable
import io.github.shaoyuanyu.ttts.persistence.student.StudentTable
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.datetime.datetime

object StudentCoachRelationTable : UUIDTable("student_coach") {

    val coachID = reference("coach_id", CoachTable, ReferenceOption.CASCADE)
//    val coach_username: Column<String> = varchar("coach_username", 64).uniqueIndex()

    val studentID = reference("student_id", StudentTable, ReferenceOption.CASCADE)
//    val student_username: Column<String> = varchar("student_username", 64).uniqueIndex()
    // TODO: 使用 enum
    val status: Column<String> = varchar("status", 32)

    val start_time: Column<LocalDateTime> = datetime("start_time")

    val end_time: Column<LocalDateTime> = datetime("end_time")

    val created_at: Column<LocalDateTime> = datetime("created_at")

}