@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.coach

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.UUID
import kotlin.time.ExperimentalTime

class CoachEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<CoachEntity>(CoachTable)

    var username by CoachTable.username
    var photo_url by CoachTable.photo_url
    var achievements by CoachTable.achievements
    var level_ by CoachTable.level_
    var hourly_rate by CoachTable.hourly_rate
    var balance by CoachTable.balance
    var max_students by CoachTable.max_students
    var current_students by CoachTable.current_students
    var is_approved by CoachTable.is_approved
    var approved_by by CoachTable.approved_by
    var createdAt by CoachTable.created_at
    var lastLoginAt by CoachTable.last_login_at

}