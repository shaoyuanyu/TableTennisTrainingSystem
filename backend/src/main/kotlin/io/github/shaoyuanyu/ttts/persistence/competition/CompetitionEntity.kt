package io.github.shaoyuanyu.ttts.persistence.competition

import io.github.shaoyuanyu.ttts.dto.competition.Competition
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class CompetitionEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<CompetitionEntity>(CompetitionTable)

    var name by CompetitionTable.name
    var type by CompetitionTable.type
    var campus by CampusEntity referencedOn CompetitionTable.campus
    var date by CompetitionTable.date
    var registrationDeadline by CompetitionTable.registrationDeadline
    var fee by CompetitionTable.fee
    var description by CompetitionTable.description
}

fun CompetitionEntity.expose() = Competition(
    id = id.value.toString(),
    name = name,
    type = type,
    campusId = campus.id.value,
    campusName = campus.campusName,
    date = date,
    registrationDeadline = registrationDeadline,
    fee = fee,
    description = description,
)

fun List<CompetitionEntity>.expose() = this.map { it.expose() }