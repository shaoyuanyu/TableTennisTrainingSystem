@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.competition
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object ComTable: IntIdTable("competition")   {

    val userId:Column<String> = varchar("user_id",128)
    val username:Column<String> = varchar("username",32)
    val group:Column<String> = varchar("group",32)
    val tableId:Column<Int> = integer("table_id")
    val campusId:Column<Int> = integer("campus_id")
    val createdAt: Column<Instant> = timestamp("created_at")

}