package io.github.shaoyuanyu.ttts.persistence.student

import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.IdTable
import org.jetbrains.exposed.v1.core.ReferenceOption
import java.util.UUID

object StudentTable: IdTable<UUID>("student")  {
    override val id = reference("user_id", UserTable, ReferenceOption.CASCADE)

    override val primaryKey = PrimaryKey(id)
    
    val balance: Column<Float> = float("balance")

    val max_coach: Column<Int> = integer("max_coach").default(2)

    val current_coach: Column<Int> = integer("current_coach")
}