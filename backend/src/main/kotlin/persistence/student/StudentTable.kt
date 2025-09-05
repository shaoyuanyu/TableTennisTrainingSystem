package persistence.student

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime


object StudentTable: UUIDTable("student")  {
    // TODO: 使用 foreign key
    val username: Column<String> = varchar("username", 64).uniqueIndex()

    val balance: Column<Float> = float("balance")

    val created_at: Column<LocalDateTime> = datetime("created_at")

    val last_login_at: Column<LocalDateTime> = datetime("last_login_at")
}