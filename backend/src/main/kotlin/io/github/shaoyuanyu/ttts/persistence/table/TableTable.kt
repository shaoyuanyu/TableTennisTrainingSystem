package io.github.shaoyuanyu.ttts.persistence.table

import io.github.shaoyuanyu.ttts.dto.table.TableOccupiedByGroup
import io.github.shaoyuanyu.ttts.dto.table.TableStatus
import io.github.shaoyuanyu.ttts.persistence.campus.CampusTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

object TableTable: UUIDTable("table")  {
    val status: Column<TableStatus> = customEnumeration(
        name = "status",
        sql = "ENUM('FREE', 'PARTLY_OCCUPIED', 'OCCUPIED')",
        fromDb = { value -> TableStatus.valueOf(value as String) },
        toDb = { it.name }
    )

    val group: Column<TableOccupiedByGroup> = customEnumeration(
        name = "group",
        sql = "ENUM('A', 'B', 'C', 'ON_CLASS', 'FREE')",
        fromDb = { value -> TableOccupiedByGroup.valueOf(value as String) },
        toDb = { it.name }
    )

    var index_in_campus: Column<Int> = integer("index")

    val campus = reference("campus", CampusTable, ReferenceOption.CASCADE)
}