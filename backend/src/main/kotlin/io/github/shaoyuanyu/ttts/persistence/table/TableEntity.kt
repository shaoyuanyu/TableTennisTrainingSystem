package io.github.shaoyuanyu.ttts.persistence.table

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class TableEntity(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<TableEntity>(TableTable)
    var status by TableTable.status
    var group by TableTable.group
    var campusId by TableTable.campusId

    }