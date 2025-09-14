@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.campus

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime


class CampusEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CampusEntity>(CampusTable)

    var campusName by CampusTable.campus_name
    var address by CampusTable.address
    var contactPerson by CampusTable.contact_person
    var phone by CampusTable.phone
    var email by CampusTable.email
    var balance by CampusTable.balance
    var isCentral by CampusTable.is_central
    var tableNumber by CampusTable.tableNumber
    var createdAt by CampusTable.created_at
    var lastLoginAt by CampusTable.last_login_at
}