package io.github.shaoyuanyu.ttts.persistence.student

import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import java.util.*

class StudentEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, StudentEntity>(StudentTable)

    var userId by UserEntity referencedOn StudentTable.id
    var balance by StudentTable.balance
    var maxCoach by StudentTable.max_coach
    var currentCoach by StudentTable.current_coach
}
