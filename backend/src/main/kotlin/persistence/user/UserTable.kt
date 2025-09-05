package io.github.shaoyuanyu.ttts.persistence.user

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime

object UserTable : UUIDTable("user") {
    val username: Column<String> = varchar("username", 64).uniqueIndex()

    /**
     * 使用 BCrypt 算法
     */
    val encrypted_password: Column<String> = text("encrypted_password")

    val real_name: Column<String> = varchar("real_name", 128)

    val gender: Column<String> = varchar("gender", 32)

    val age: Column<Int> = integer("age")

    val phone_number: Column<String> = varchar("phone_number", 16)

    val email: Column<String> = varchar("email", 128)

    // TODO: 使用 foreign key
    val campus_id: Column<Int> = integer("campus_id")

    // TODO: 使用 enum
    val status: Column<String> = varchar("status", 32)

    val created_at: Column<LocalDateTime> = datetime("created_at")

    val last_login_at: Column<LocalDateTime> = datetime("last_login_at")
}