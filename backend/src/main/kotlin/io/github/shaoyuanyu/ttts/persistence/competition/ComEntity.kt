@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.competition


import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

class ComEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ComEntity>(ComTable)
    var userId by ComTable.userId
    var username by ComTable.username
    var group by ComTable.group
    var tableId by ComTable.tableId
    var campusId by ComTable.campusId
    var status by ComTable.status
    var createdAt by ComTable.createdAt
}
