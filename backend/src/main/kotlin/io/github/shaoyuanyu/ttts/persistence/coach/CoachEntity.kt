package io.github.shaoyuanyu.ttts.persistence.coach

import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityClass
import java.util.UUID

class CoachEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, CoachEntity>(CoachTable)

    var userId by UserEntity referencedOn CoachTable.id
    var photoUrl by CoachTable.photo_url
    var achievements by CoachTable.achievements
    var level by CoachTable.level
    var hourlyRate by CoachTable.hourly_rate
    var balance by CoachTable.balance
    var maxStudents by CoachTable.max_students
    var currentStudents by CoachTable.current_students
    var isApproved by CoachTable.is_approved
    var approvedBy by CoachTable.approved_by
}