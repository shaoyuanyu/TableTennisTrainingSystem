package io.github.shaoyuanyu.ttts.dto.user

import io.github.shaoyuanyu.ttts.dto.student.StudentInfo
import io.github.shaoyuanyu.ttts.dto.coach.CoachInfo
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uuid: String? = null,
    val username: String,
    val plainPassword: String? = null,
    val encryptedPassword: String? = null,
    val realName: String,
    val gender: String,
    val age: Int,
    val phoneNumber: String,
    val email: String,
    val campusId: Int,
    val campusName: String? = null,
    val role: UserRole,
    val status: String,
    val createdAt: LocalDateTime? = null,
    val lastLoginAt: LocalDateTime? = null,
    
    // 角色特定信息
    val studentInfo: StudentInfo? = null,
    val coachInfo: CoachInfo? = null,
)

fun User.injectStudentEntity(studentEntity: StudentEntity) =
    this.copy(
        studentInfo = StudentInfo(
            balance = studentEntity.balance,
            maxCoach = studentEntity.maxCoach,
            currentCoach = studentEntity.currentCoach
        )
    )

fun User.injectCoachEntity(coachEntity: CoachEntity) =
    this.copy(
        coachInfo = CoachInfo(
            balance = coachEntity.balance,
            hourlyRate = coachEntity.hourlyRate,
            maxStudents = coachEntity.maxStudents,
            currentStudents = coachEntity.currentStudents,
            isApproved = coachEntity.isApproved,
            approvedBy = coachEntity.approvedBy,
            photoUrl = coachEntity.photoUrl,
            achievements = coachEntity.achievements,
            level = coachEntity.level,
        )
    )
