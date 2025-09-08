@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.student

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.Instant
import kotlin.time.ExperimentalTime


object StudentTable: UUIDTable("student")  {
    // TODO: 使用 foreign key
    val username: Column<String> = varchar("username", 64).uniqueIndex()

    val balance: Column<Float> = float("balance")

    val max_coach: Column<Int> = integer("max_coach").default(2)

    val current_coach: Column<Int> = integer("current_coach")

    val created_at: Column<Instant> = timestamp("created_at")

    val last_login_at: Column<Instant> = timestamp("last_login_at")
}