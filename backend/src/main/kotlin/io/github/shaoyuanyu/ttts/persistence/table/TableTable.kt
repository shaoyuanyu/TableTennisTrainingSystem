package io.github.shaoyuanyu.ttts.persistence.table

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object TableTable: IntIdTable("table")  {
    val  status:Column<String> = varchar("status", 32)
    val group:Column<String> = varchar("group", 32)
    val campusId:Column<Int> = integer("campus_id")
}