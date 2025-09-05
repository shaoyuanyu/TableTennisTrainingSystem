package persistence.user

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class CampusEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<CampusEntity>(CampusTable)

    val campus_name by CampusTable.campus_name
    val address by CampusTable.address
    val contact_person by CampusTable.contact_person
    val phone by CampusTable.phone
    val email by CampusTable.email
    val is_central by CampusTable.is_central
    var created_at by CampusTable.created_at
    var last_login_at by CampusTable.last_login_at

}