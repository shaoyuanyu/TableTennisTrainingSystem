@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.recharge


import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object RechargeTable : IntIdTable("recharge") {

    val userId: Column<String> = varchar("user_id", 64)

    val amount: Column<Float> = float("amount")

    val createdAt: Column<Instant> = timestamp("created_at")
}