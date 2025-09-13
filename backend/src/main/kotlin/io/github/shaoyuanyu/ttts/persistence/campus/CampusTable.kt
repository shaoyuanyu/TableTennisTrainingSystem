@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.campus

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object CampusTable : IntIdTable("campus") {

    val campus_name: Column<String> = varchar("campus_name", 64)

    val address: Column<String> = varchar("address", 64)

    //TODO foreign key
    val contact_person: Column<String> = varchar("contact_person", 32)

    val phone: Column<String> = varchar("phone", 32)

    val email: Column<String> = varchar("email", 32)

    val balance: Column<Float> = float("balance").default(0.0f)

    val is_central: Column<Boolean> = bool("is_central").default(false)

    val created_at: Column<Instant> = timestamp("created_at")

    val last_login_at: Column<Instant> = timestamp("last_login_at")
}