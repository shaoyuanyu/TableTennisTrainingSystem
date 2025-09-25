package io.github.shaoyuanyu.ttts.persistence.competition

import io.github.shaoyuanyu.ttts.dto.competition.Competition
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID

class CompetitionEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<CompetitionEntity>(CompetitionTable)

    var name by CompetitionTable.name
    var type by CompetitionTable.type
    var campusId by CompetitionTable.campusId
    var date by CompetitionTable.date
    var registrationDeadline by CompetitionTable.registrationDeadline
    var fee by CompetitionTable.fee
    var description by CompetitionTable.description
}

fun CompetitionEntity.expose() = Competition(
    id = this.id.value.toString(),
    name = this.name,
    type = this.type,
    campusId = this.campusId,
    date = this.date,
    registrationDeadline = this.registrationDeadline,
    fee = this.fee,
    description = this.description,
)