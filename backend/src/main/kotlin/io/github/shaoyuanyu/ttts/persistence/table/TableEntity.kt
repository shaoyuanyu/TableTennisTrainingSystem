package io.github.shaoyuanyu.ttts.persistence.table

import io.github.shaoyuanyu.ttts.dto.table.Table
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import java.util.UUID

class TableEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, TableEntity>(TableTable)

    var status by TableTable.status
    var group by TableTable.group
    var indexInCampus by TableTable.index_in_campus
    var campus by CampusEntity referencedOn TableTable.campus
}

fun TableEntity.expose() = Table(
    id = id.value.toString(),
    status = status.name,
    group = group.name,
    indexInCampus = indexInCampus,
    campusId = campus.id.value,
    campusName = campus.campusName
)