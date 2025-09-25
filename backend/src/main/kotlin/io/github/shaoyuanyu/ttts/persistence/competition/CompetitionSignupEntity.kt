@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence.competition

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

    var campusId by CompetitionSignupTable.campusId

    var status by CompetitionSignupTable.status

    var createdAt by CompetitionSignupTable.createdAt
}
