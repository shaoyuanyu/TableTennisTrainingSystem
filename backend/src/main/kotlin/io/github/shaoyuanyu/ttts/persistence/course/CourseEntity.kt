package io.github.shaoyuanyu.ttts.persistence.course

import io.github.shaoyuanyu.ttts.dto.course.Course
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import java.util.*

class CourseEntity(uuid: EntityID<UUID>) : UUIDEntity(uuid) {
    companion object : UUIDEntityClass<CourseEntity>(CourseTable)

    var title by CourseTable.title
    var description by CourseTable.description
    var type by CourseTable.type
    var level by CourseTable.level
    var date by CourseTable.date
    var startTime by CourseTable.startTime
    var endTime by CourseTable.endTime
    var duration by CourseTable.duration
    var location by CourseTable.location
    var status by CourseTable.status
    var maxStudents by CourseTable.maxStudents
    var currentStudents by CourseTable.currentStudents
    var price by CourseTable.price
    var coachId by CourseTable.coachId
    var coachName by CourseTable.coachName
    var campusId by CourseTable.campusId
    var campusName by CourseTable.campusName
    var createdAt by CourseTable.createdAt
    var updatedAt by CourseTable.updatedAt
}

fun CourseEntity.expose() = Course(
    id = this.id.value.toString(),
    title = this.title,
    description = this.description,
    type = this.type,
    level = this.level,
    date = this.date.toString(),
    startTime = this.startTime.toString(),
    endTime = this.endTime.toString(),
    duration = this.duration,
    location = this.location,
    status = this.status,
    maxStudents = this.maxStudents,
    currentStudents = this.currentStudents,
    price = this.price,
    coachId = this.coachId.toString(),
    coachName = this.coachName,
    campusId = this.campusId,
    campusName = this.campusName,
    createdAt = this.createdAt.toString(),
    updatedAt = this.updatedAt.toString()
)