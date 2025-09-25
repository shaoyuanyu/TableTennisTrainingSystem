package io.github.shaoyuanyu.ttts.persistence.competition

import io.github.shaoyuanyu.ttts.dto.competition.CompetitionArrangement
import io.github.shaoyuanyu.ttts.persistence.table.TableEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass

class CompetitionArrangementEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CompetitionArrangementEntity>(CompetitionArrangementTable)

    var competition by CompetitionEntity referencedOn CompetitionArrangementTable.competition

    var turnNumber by CompetitionArrangementTable.turnNumber

    var table by TableEntity referencedOn CompetitionArrangementTable.table

    var playerA by UserEntity referencedOn CompetitionArrangementTable.playerA

    var playerB by UserEntity referencedOn CompetitionArrangementTable.playerB

    var status by CompetitionArrangementTable.status

    var result by CompetitionArrangementTable.result

    var winner by CompetitionArrangementTable.winner
}

fun CompetitionArrangementEntity.expose() = CompetitionArrangement(
    competitionId = competition.id.value.toString(),
    competitionName = competition.name,
    competitionType = competition.type,
    campusId = competition.campus.id.value,
    campusName = competition.campus.campusName,
    date = competition.date,
    turnNumber = turnNumber,
    tableId = table.indexInCampus,
    userIdOfPlayerA = playerA.id.value.toString(),
    realNameOfPlayerA = playerA.realName,
    userIdOfPlayerB = playerB.id.value.toString(),
    realNameOfPlayerB = playerB.realName,
    status = status,
    result = result,
    winner = when (winner) {
        1 -> playerA.realName
        2 -> playerB.realName
        else -> ""
    }
)

fun List<CompetitionArrangementEntity>.expose() = map { it.expose() }