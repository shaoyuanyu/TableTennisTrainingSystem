package io.github.shaoyuanyu.ttts.dto.competition

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class CompetitionArrangement(
    val competitionId: String,
    val competitionName: String,
    val competitionType: String,
    val campusId: Int,
    val campusName: String,
    val date: LocalDate,
    val turnNumber: Int,
    val tableId: Int,
    val userIdOfPlayerA: String,
    val realNameOfPlayerA: String,
    val userIdOfPlayerB: String,
    val realNameOfPlayerB: String,
    val status: String,
    val result: String,
    val winner: String
)
