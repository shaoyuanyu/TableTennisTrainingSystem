package io.github.shaoyuanyu.ttts.persistence.table

import io.github.shaoyuanyu.ttts.dto.student.Group
import io.github.shaoyuanyu.ttts.dto.student.Status
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable


object TableTable: UUIDTable("table")  {
    val status:Column<Status> = customEnumeration(
        name="status",
        sql="ENUM('free','partlyoccupy','occupy')",
        fromDb = { value -> Status.valueOf(value as String) },
        toDb = { it.name }
    )
    val group: Column<Group> = customEnumeration(
        name = "group",
        sql = "ENUM('A', 'B', 'C', 'onclass','free')",
        fromDb = { value -> Group.valueOf(value as String) },
        toDb = { it.name }
    )

    var index_in_campus: Column<Int> = integer("index")
    val campusId: Column<Int> = integer("campus_id")
}