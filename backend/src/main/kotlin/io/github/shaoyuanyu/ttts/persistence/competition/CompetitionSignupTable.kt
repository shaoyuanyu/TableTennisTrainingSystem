@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence.competition

import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object CompetitionSignupTable: IntIdTable("competition_signup")   {

    val competition = reference("competition", CompetitionTable, ReferenceOption.CASCADE)

    val user = reference("user", UserTable, ReferenceOption.CASCADE)

    val group: Column<String> = varchar("group",32)

    val campusId: Column<Int> = integer("campus_id")

    val status: Column<String> = varchar("status",32)

    val createdAt: Column<Instant> = timestamp("created_at")
}