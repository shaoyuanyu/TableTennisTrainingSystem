package io.github.shaoyuanyu.ttts.persistence.coach

import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import java.util.UUID

object CoachTable : IdTable<UUID>("coach") {
    override val id = reference("user_id", UserTable, ReferenceOption.CASCADE)
    
    override val primaryKey = PrimaryKey(id)

    val photo_url: Column<String> = varchar("photo_url", 128)

    val achievements: Column<String> = varchar("achievements", 128)

    val level: Column<String> = varchar("level",32)

    val hourly_rate: Column<Float> = float("hourly_rate")

    val balance: Column<Float> = float("balance")

    val max_students: Column<Int> = integer("max_students").default(20)

    val current_students: Column<Int> = integer("current_students")

    val is_approved: Column<Boolean> = bool("is_approved").default(false)

    val approved_by: Column<String> = varchar("approved_by", 64)
}