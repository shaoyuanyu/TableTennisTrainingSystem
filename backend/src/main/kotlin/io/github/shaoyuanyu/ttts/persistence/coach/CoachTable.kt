@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.coach

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object CoachTable : UUIDTable("coach") {

    val username: Column<String> = varchar("username", 64).uniqueIndex()
    /**
     * 使用 BCrypt 算法
     */
    val photo_url: Column<String> = varchar("photo_url", 128)

    val achievements: Column<String> = varchar("achievements", 128)

    val level_: Column<String> = varchar("level_",32)

    val hourly_rate: Column<Float> = float("hourly_rate")

    val balance: Column<Float> = float("balance")

    val max_students: Column<Int> = integer("max_students").default(20)

    val current_students: Column<Int> = integer("current_students")

    val is_approved: Column<Boolean> = bool("is_approved").default(false)

    // TODO: 使用 foreign key
    val approved_by: Column<Int> = integer("approved_by")

    val created_at: Column<Instant> = timestamp("created_at")

    val last_login_at: Column<Instant> = timestamp("last_login_at")
}