@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.coach.CoachApproved
import io.github.shaoyuanyu.ttts.dto.coach.CoachRecord
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.dto.user.injectCoachEntity
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.persistence.user.exposeWithoutPassword
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.ExperimentalTime

internal val COACH_SERVICE_LOGGER: Logger = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.persistence.coach")

class CoachService(
    private val database: Database,
    private val userService: UserService
) {
    /**
     * 通过审批并更新教练等级
     */
    fun updateCoachLevel(coachId: String, adminId: String,newLevel: String) {
        transaction(database) {
            val coachEntity = CoachEntity.findById(UUID.fromString(coachId))
                ?: throw IllegalArgumentException("教练不存在")

            // 根据等级设置对应的时薪
            val hourlyRate = when (newLevel) {
                "初级教练员" -> 80.0f
                "中级教练员" -> 150.0f
                "高级教练员" -> 200.0f
                else -> throw IllegalArgumentException("无效的教练等级：$newLevel。支持的等级：初级、中级、高级")
            }

            coachEntity.isApproved = true
            coachEntity.level = newLevel
            coachEntity.hourlyRate = hourlyRate
            coachEntity.approvedBy=adminId

            USER_LOGGER.info("更新教练等级成功，教练ID：$coachId，新等级：$newLevel，新时薪：$hourlyRate")
        }
    }

    /**
     * 获取管理员所在校区未审核通过的教练信息
     */
    fun getCampusCoachNotApproved(adminId: String, page: Int, size: Int): List<CoachApproved> =
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
                .filter { it.coachInfo?.isApproved == false }
                .sortedBy { it.createdAt }
                .drop(offset)
                .take(size)
                .map { user ->
                    CoachApproved(
                        uuid = user.uuid.toString(),
                        realName = user.realName,
                        gender = user.gender,
                        age = user.age,
                        phoneNumber = user.phoneNumber,
                        email = user.email,
                        photoUrl = user.coachInfo?.photoUrl ?: "",
                        achievements = user.coachInfo?.achievements ?: "",
                    )
                }
        }

    /**
     * 获取所有未审核通过的教练信息(超级管理员)
     */
    fun getAllCoachNotApproved(page: Int,size: Int): List<CoachApproved> =
        transaction(database) {
            val offset = (page-1) * size
            UserEntity.all()
                .filter { user -> user.role == UserRole.COACH }
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
                .filter { it.coachInfo?.isApproved == false }
                .sortedBy { it.createdAt }
                .drop(offset)
                .take(size)
                .map { user ->
                    CoachApproved(
                        uuid= user.uuid.toString(),
                        realName = user.realName,
                        gender = user.gender,
                        age = user.age,
                        phoneNumber = user.phoneNumber,
                        email = user.email,
                        photoUrl = user.coachInfo?.photoUrl ?: "",
                        achievements = user.coachInfo?.achievements ?: "",
                    )
                }
        }

    /**
     * 通过用户名查询教练信息
     */
    fun queryCoachesByUsername(username: String): CoachRecord =
        transaction(database) {
            val coach = userService.queryUserByUsername(username)

            if(coach.username.isBlank()){
                throw IllegalArgumentException("用户不存在")
            }

            if (coach.role != UserRole.COACH) {
                throw IllegalArgumentException("用户不是教练")
            }

            val coachEntity = CoachEntity.findById(UUID.fromString(coach.uuid))
            val userWithCoachInfo = if (coachEntity != null) {
                coach.injectCoachEntity(coachEntity)
            } else {
                coach
            }
            if(userWithCoachInfo.coachInfo?.isApproved!=true){
                throw IllegalArgumentException("教练尚未通过审核")
            }

            CoachRecord(
                coachId = userWithCoachInfo.uuid.toString(),
                username = userWithCoachInfo.username,
                realName = userWithCoachInfo.realName,
                gender = userWithCoachInfo.gender,
                age = userWithCoachInfo.age,
                campusId = userWithCoachInfo.campusId,
                photoUrl = userWithCoachInfo.coachInfo.photoUrl ?: "",
                achievements = userWithCoachInfo.coachInfo.achievements ?:"",
                level = userWithCoachInfo.coachInfo.level ?: "",
                hourlyRate = userWithCoachInfo.coachInfo.hourlyRate,
                currentStudents = userWithCoachInfo.coachInfo.currentStudents
            )
        }
    /**
     * 根据uuid查询教练信息
     */
    fun queryCoachByUuid(uuid: String): CoachRecord =
        transaction(database) {
            val coach = userService.queryUserByUUID(uuid)

            if(coach.username.isBlank()){
                throw IllegalArgumentException("用户不存在")
            }

            if (coach.role != UserRole.COACH) {
                throw IllegalArgumentException("用户不是教练")
            }

            val coachEntity = CoachEntity.findById(UUID.fromString(coach.uuid))
            val userWithCoachInfo = if (coachEntity != null) {
                coach.injectCoachEntity(coachEntity)
            } else {
                coach
            }
            if(userWithCoachInfo.coachInfo?.isApproved!=true){
                throw IllegalArgumentException("教练尚未通过审核")
            }

            CoachRecord(
                coachId = userWithCoachInfo.uuid.toString(),
                username = userWithCoachInfo.username,
                realName = userWithCoachInfo.realName,
                gender = userWithCoachInfo.gender,
                age = userWithCoachInfo.age,
                campusId = userWithCoachInfo.campusId,
                photoUrl = userWithCoachInfo.coachInfo.photoUrl ?: "",
                achievements = userWithCoachInfo.coachInfo.achievements ?:"",
                level = userWithCoachInfo.coachInfo.level ?: "",
                hourlyRate = userWithCoachInfo.coachInfo.hourlyRate,
                currentStudents = userWithCoachInfo.coachInfo.currentStudents
            )
        }


    /**
     * 分页获取所有教练信息
     */
    fun getAllCoaches(page: Int, size: Int): List<CoachRecord> =
        transaction(database) {
            val offset = (page - 1) * size

            UserEntity.all()
                .filter { user -> user.role == UserRole.COACH }
                .sortedBy { it.createdAt }
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
                .toList()
                .drop(offset)
                .take(size)
                .filter { it.coachInfo?.isApproved == true }
                .map { user ->
                    CoachRecord(
                        coachId = user.uuid.toString(),
                        username = user.username,
                        realName = user.realName,
                        gender = user.gender,
                        age = user.age,
                        campusId = user.campusId,
                        photoUrl = user.coachInfo?.photoUrl ?: "",
                        achievements = user.coachInfo?.achievements ?:"",
                        level = user.coachInfo?.level ?: "",
                        hourlyRate = user.coachInfo?.hourlyRate ?: 0.0f,
                        currentStudents = user.coachInfo?.currentStudents ?: 0
                    )
                }
        }
}