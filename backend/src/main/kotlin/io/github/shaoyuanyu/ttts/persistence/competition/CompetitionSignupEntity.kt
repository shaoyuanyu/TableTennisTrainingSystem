@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence.competition

import io.github.shaoyuanyu.ttts.dto.competition.CompetitionSignup
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

class CompetitionSignupEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CompetitionSignupEntity>(CompetitionSignupTable)

    var competition by CompetitionEntity referencedOn CompetitionSignupTable.competition

    var user by UserEntity referencedOn CompetitionSignupTable.user

    var group by CompetitionSignupTable.group

    var status by CompetitionSignupTable.status

    var createdAt by CompetitionSignupTable.createdAt
}

fun CompetitionSignupEntity.expose() = CompetitionSignup(
    competitionId = competition.id.value.toString(),
    competitionName = competition.name,
    userId = user.id.value.toString(),
    group = group,
    campusId = competition.campus.id.value,
    campusName = competition.campus.campusName,
    status = status,
    createdAt = createdAt.toString()
)

fun List<CompetitionSignupEntity>.expose() = map { it.expose() }