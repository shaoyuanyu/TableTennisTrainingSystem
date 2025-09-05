package persistence.user

import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.datetime

object CampusTable : UUIDTable("campus") {

    val campus_name: Column<String> = varchar("campus_name", 64)

    val address: Column<String> = varchar("address", 64)

    //TODO foreign key
    val contact_person: Column<Int> = integer("contact_person")

    val phone: Column<String> = varchar("phone", 32)

    val email: Column<String> = varchar("email", 32)

    val is_central: Column<Boolean> = bool("is_central").default(false)

    val created_at: Column<LocalDateTime> = datetime("created_at")

    val last_login_at: Column<LocalDateTime> = datetime("last_login_at")
}