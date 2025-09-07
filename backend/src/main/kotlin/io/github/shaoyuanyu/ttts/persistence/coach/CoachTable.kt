package io.github.shaoyuanyu.ttts.persistence.coach

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime

object CoachTable : UUIDTable("coach") {

    val username: Column<String> = varchar("username", 64).uniqueIndex()
    /**
     * 使用 BCrypt 算法
     */
    val photo_url: Column<String> = varchar("photo_url", 128)

    val achievements: Column<String> = varchar("achievements", 128)

    val level_: Column<String> = varchar("level_",32)

    val hourly_rate: Column<Float> = float("hourly_rate")

    val max_students: Column<Int> = integer("max_students").default(20)

    val current_students: Column<Int> = integer("current_students").default(0)

    val is_approved: Column<Boolean> = bool("is_approved").default(false)

    // TODO: 使用 foreign key
    val approved_by: Column<Int> = integer("approved_by")

    val created_at: Column<LocalDateTime> = datetime("created_at")

    val last_login_at: Column<LocalDateTime> = datetime("last_login_at")
}