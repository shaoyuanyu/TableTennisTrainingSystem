@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence.competition

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass
import kotlin.time.ExperimentalTime

class CompetitionSignupEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CompetitionSignupEntity>(CompetitionSignupTable)
    var userId by CompetitionSignupTable.userId
    var username by CompetitionSignupTable.username
    var group by CompetitionSignupTable.group
    var tableId by CompetitionSignupTable.tableId
    var campusId by CompetitionSignupTable.campusId
    var status by CompetitionSignupTable.status
    var createdAt by CompetitionSignupTable.createdAt
}
