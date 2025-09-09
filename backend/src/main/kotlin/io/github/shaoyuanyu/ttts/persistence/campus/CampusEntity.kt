@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.campus

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID
import kotlin.time.ExperimentalTime

//class CampusEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
//    companion object : UUIDEntityClass<CampusEntity>(CampusTable)
//
//    var campus_name by CampusTable.campus_name
//    var address by CampusTable.address
//    var contact_person by CampusTable.contact_person
//    var phone by CampusTable.phone
//    var email by CampusTable.email
//    var balance by CampusTable.balance
//    var is_central by CampusTable.is_central
//    var createdAt by CampusTable.created_at
//    var lastLoginAt by CampusTable.last_login_at
//
//}
class CampusEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CampusEntity>(CampusTable)

    var campus_name by CampusTable.campus_name
    var address by CampusTable.address
    var contact_person by CampusTable.contact_person
    var phone by CampusTable.phone
    var email by CampusTable.email
    var balance by CampusTable.balance
    var is_central by CampusTable.is_central
    var createdAt by CampusTable.created_at
    var lastLoginAt by CampusTable.last_login_at
}