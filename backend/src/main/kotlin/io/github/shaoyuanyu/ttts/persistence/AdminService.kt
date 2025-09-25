@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.coach.CoachRecord
import io.github.shaoyuanyu.ttts.dto.student.StudentRecord
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.injectCoachEntity
import io.github.shaoyuanyu.ttts.dto.user.injectStudentEntity
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.persistence.user.exposeWithoutPassword
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.ExperimentalTime

internal val ADMIN_SERVICE_LOGGER: Logger = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.persistence.admin")

class AdminService(
    private val database: Database
) {
    /**
     * 获取管理员所在校区的所有学生
     */
    fun getCampusStudents(adminId: String, page: Int, size: Int): List<StudentRecord> =
        transaction(database) {
            // 获取管理员所属校区ID
            val adminEntity = UserEntity.findById(UUID.fromString(adminId))
                ?: throw IllegalArgumentException("管理员不存在")
            val adminCampusId = adminEntity.campusId

            val offset = (page - 1) * size

            UserEntity.all()
                .filter { user ->
                    user.role == UserRole.STUDENT &&
                            user.campusId == adminCampusId // 只筛选同校区的学生
                }
                .sortedBy { it.createdAt }
                .drop(offset)
                .take(size)
                .map { userEntity ->
                    val user = userEntity.exposeWithoutPassword()
                    val studentEntity = StudentEntity.findById(userEntity.id.value)
                    val userWithStudentInfo = if (studentEntity != null) {
                        user.injectStudentEntity(studentEntity)
                    } else {
                        user
                    }
                    userWithStudentInfo
                }
                .map { user ->
                    StudentRecord(
                        studentId = user.uuid.toString(),
                        username = user.username,
                        realName = user.realName,
                        gender = user.gender,
                        age = user.age,
                        campusId = user.campusId,
                        photoUrl = "",
                        phoneNumber = user.phoneNumber,
                        skillLevel = "",
                        balance = user.studentInfo?.balance ?: 0.0f,
                        maxCoach = user.studentInfo?.maxCoach ?: 0,
                        currentCoach = user.studentInfo?.currentCoach ?: 0,
                        createdAt = user.createdAt.toString()
                    )
                }
        }

    /**
     * 获取管理员所在校区的所有教练
     */
    fun getCampusCoaches(adminId: String, page: Int, size: Int): List<CoachRecord> =
        transaction(database) {
            // 获取管理员所属校区ID
            val adminEntity = UserEntity.findById(UUID.fromString(adminId))
                ?: throw IllegalArgumentException("管理员不存在")
            val adminCampusId = adminEntity.campusId

            val offset = (page - 1) * size

            UserEntity.all()
                .filter { user ->
                    user.role == UserRole.COACH &&
                            user.campusId == adminCampusId // 只筛选同校区的教练
                }
                .sortedBy { it.createdAt }
                .drop(offset)
                .take(size)
                .map { userEntity ->
                    val user = userEntity.exposeWithoutPassword()
                    val coachEntity = CoachEntity.findById(userEntity.id.value)
                    val userWithCoachInfo = if (coachEntity != null) {
                        user.injectCoachEntity(coachEntity)
                    } else {
                        user
                    }
                    userWithCoachInfo
                }
                .filter { it.coachInfo?.isApproved == true } // 只返回已审核通过的教练
                .map { user ->
                    CoachRecord(
                        coachId = user.uuid.toString(),
                        username = user.username,
                        realName = user.realName,
                        gender = user.gender,
                        age = user.age,
                        isApproved = user.coachInfo?.isApproved ?: false,
                        approvedBy = user.coachInfo?.approvedBy,
                        campusId = user.campusId,
                        phoneNumber = user.phoneNumber,
                        photoUrl = user.coachInfo?.photoUrl ?: "",
                        achievements = user.coachInfo?.achievements ?:"",
                        level = user.coachInfo?.level ?: "",
                        hourlyRate = user.coachInfo?.hourlyRate ?: 0.0f,
                        currentStudents = user.coachInfo?.currentStudents ?: 0,
                        maxStudents = user.coachInfo?.maxStudents ?: 0,
                        createdAt = user.createdAt.toString()
                    )
                }
        }
}