package io.github.shaoyuanyu.ttts.persistence.competition

import io.github.shaoyuanyu.ttts.persistence.table.TableTable
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable

object CompetitionArrangementTable : IntIdTable("competition_arrangement") {
    val competition = reference("competition", CompetitionTable, ReferenceOption.CASCADE)

    val turnNumber: Column<Int> = integer("turn_number")

    val table = reference("table", TableTable, ReferenceOption.CASCADE)

    val playerA = reference("player_a", UserTable, ReferenceOption.CASCADE)

    val playerB = reference("player_b", UserTable, ReferenceOption.CASCADE)

    val status: Column<String> = varchar("status", 32)

    val result: Column<String> = varchar("result", 32)

    val winner = reference("winner", UserTable, ReferenceOption.CASCADE)
}