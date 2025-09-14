@file:OptIn(kotlin.time.ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.mutual_selection.MutualSelection
import io.github.shaoyuanyu.ttts.dto.mutual_selection.MutualSelectionStatus
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.mutual_selection.MutualSelectionEntity
import io.github.shaoyuanyu.ttts.persistence.mutual_selection.MutualSelectionTable
import io.github.shaoyuanyu.ttts.persistence.mutual_selection.expose
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.Clock
import kotlin.time.Instant

internal val MUTUAL_SELECTION_LOGGER = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.persistence.mutual_selection")

class MutualSelectionService(
    private val database: Database
) {
    /**
     * 学生申请教练
     */
    fun applyForCoach(
        studentId: String,
        coachId: String,
        expectedStartTime: Instant
    ): MutualSelection =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentId))
                ?: throw IllegalArgumentException("学生不存在")
            val coach = CoachEntity.findById(UUID.fromString(coachId))
                ?: throw IllegalArgumentException("教练不存在")

            // 检查学生是否已达到最大教练数
            if (student.currentCoach >= student.maxCoach) {
                throw IllegalArgumentException("学生已达到最大可选教练数")
            }

            // 检查教练是否已达到最大学生数
            if (coach.currentStudents >= coach.maxStudents) {
                throw IllegalArgumentException("教练已达到最大可接收学生数")
            }

            // 检查是否存在相同状态的关系
            val existingRelation = MutualSelectionEntity.find {
                (MutualSelectionTable.student_id eq student.id) and
                        (MutualSelectionTable.coach_id eq coach.id) and
                        (MutualSelectionTable.status inList listOf(
                            MutualSelectionStatus.PENDING,
                            MutualSelectionStatus.APPROVED,
                            MutualSelectionStatus.ACTIVE
                        ))
            }.firstOrNull()

            if (existingRelation != null) {
                throw IllegalArgumentException("已存在相同状态的申请或关系")
            }

            // 创建新的申请关系
            val relation = MutualSelectionEntity.new {
                this.studentID = student
                this.coachID = coach
                this.status = MutualSelectionStatus.PENDING
                this.applicationTime = Clock.System.now()
                this.expectedStartTime = expectedStartTime
            }

            MUTUAL_SELECTION_LOGGER.info("学生申请教练成功")

            relation.expose()
        }

    /**
     * 获取学生申请记录
     */
    fun getStudentApplications(
        studentUUID: String,
        page: Int,
        size: Int
    ): List<MutualSelection> =
        transaction(database) {
            val query = MutualSelectionEntity.find { MutualSelectionTable.student_id eq UUID.fromString(studentUUID) }

            // 计算偏移量
            val offset = (page - 1) * size

            // 查询分页数据
            query
                .sortedByDescending { it.applicationTime }
                .drop(offset)
                .take(size)
                .toList()
                .expose()
        }

    /**
     * 获取教练收到的申请记录
     */
    fun getCoachApplications(
        coachUUID: String,
        status: String?,
        page: Int,
        size: Int
    ): List<MutualSelection> =
        transaction(database) {
            val coach = CoachEntity.findById(UUID.fromString(coachUUID))
                ?: throw IllegalArgumentException("教练不存在")

            val query = if (status != null) {
                MutualSelectionEntity.find {
                    (MutualSelectionTable.coach_id eq coach.id) and
                            (MutualSelectionTable.status eq MutualSelectionStatus.valueOf(status.uppercase()))
                }
            } else {
                MutualSelectionEntity.find {
                    MutualSelectionTable.coach_id eq coach.id
                }
            }

            // 计算偏移量
            val offset = (page - 1) * size

            // 查询分页数据
            query
                .sortedByDescending { it.applicationTime }
                .drop(offset)
                .take(size)
                .toList()
                .expose()
        }

    /**
     * 教练审核学生申请
     */
    fun reviewApplication(
        coachUUID: String,
        relationId: String,
        approve: Boolean
    ): MutualSelection =
        transaction(database) {
            val coach = CoachEntity.findById(UUID.fromString(coachUUID))
                ?: throw IllegalArgumentException("教练不存在")

            val relation = MutualSelectionEntity.findById(UUID.fromString(relationId))
                ?: throw IllegalArgumentException("申请记录不存在")

            // 验证该申请是否属于该教练
            if (relation.coachID.id != coach.id) {
                throw SecurityException("无权限审核该申请")
            }

            // 验证申请状态是否为待处理
            if (relation.status != MutualSelectionStatus.PENDING) {
                throw IllegalArgumentException("该申请状态不为待处理状态")
            }

            // 更新申请状态
            relation.status = if (approve) {
                // 如果批准，更新为已批准状态
                MutualSelectionStatus.APPROVED
            } else {
                // 如果拒绝，更新为已拒绝状态
                MutualSelectionStatus.REJECTED
            }

            // 如果批准了申请，更新学生和教练的计数
            if (approve) {
                relation.actualStartTime = Clock.System.now()
                relation.studentID.currentCoach += 1
                relation.coachID.currentStudents += 1
            }

            MUTUAL_SELECTION_LOGGER.info("教练审核学生申请完成")

            relation.expose()
        }

    /**
     * 学生撤回申请
     */
    fun withdrawApplication(
        studentUUID: String,
        relationId: String
    ): MutualSelection =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentUUID))
                ?: throw IllegalArgumentException("学生不存在")

            val relation = MutualSelectionEntity.findById(UUID.fromString(relationId))
                ?: throw IllegalArgumentException("申请记录不存在")

            // 验证该申请是否属于该学生
            if (relation.studentID.id != student.id) {
                throw SecurityException("无权限撤回该申请")
            }

            // 验证申请状态是否为待处理
            if (relation.status != MutualSelectionStatus.PENDING) {
                throw IllegalArgumentException("该申请状态不为待处理状态，无法撤回")
            }

            // 更新申请状态为非活跃
            relation.status = MutualSelectionStatus.INACTIVE

            MUTUAL_SELECTION_LOGGER.info("学生撤回申请完成")

            relation.expose()
        }

    /**
     * 学生取消已批准的关系
     */
    fun cancelApprovedRelation(
        studentUUID: String,
        relationId: String
    ): MutualSelection =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentUUID))
                ?: throw IllegalArgumentException("学生不存在")

            val relation = MutualSelectionEntity.findById(UUID.fromString(relationId))
                ?: throw IllegalArgumentException("关系记录不存在")

            // 验证该关系是否属于该学生
            if (relation.studentID.id != student.id) {
                throw SecurityException("无权限操作该关系")
            }

            // 验证关系状态是否为已批准或活跃状态
            if (relation.status != MutualSelectionStatus.APPROVED && relation.status != MutualSelectionStatus.ACTIVE) {
                throw IllegalArgumentException("该关系状态不为已批准或活跃状态，无法取消")
            }

            // 更新关系状态为非活跃
            relation.status = MutualSelectionStatus.INACTIVE
            relation.endTime = Clock.System.now()

            // 更新学生和教练的计数
            relation.studentID.currentCoach -= 1
            relation.coachID.currentStudents -= 1

            MUTUAL_SELECTION_LOGGER.info("学生取消已批准的关系完成")

            relation.expose()
        }

    /**
     * 获取教练待处理申请数量
     */
    fun getPendingApplicationCount(coachUUID: String): Int =
        transaction(database) {
            val coach = CoachEntity.findById(UUID.fromString(coachUUID))
                ?: throw IllegalArgumentException("教练不存在")

            MutualSelectionEntity.find {
                (MutualSelectionTable.coach_id eq coach.id) and
                        (MutualSelectionTable.status eq MutualSelectionStatus.PENDING)
            }.count().toInt()
        }
    
    /**
     * 获取学生待处理申请数量
     */
    fun getStudentPendingApplicationCount(studentUUID: String): Int =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentUUID))
                ?: throw IllegalArgumentException("学生不存在")

            MutualSelectionEntity.find {
                (MutualSelectionTable.student_id eq student.id) and
                        (MutualSelectionTable.status eq MutualSelectionStatus.PENDING)
            }.count().toInt()
        }

    /**
     * 管理员直接建立关系
     */
    fun adminCreateRelation(
        studentUUID: String,
        coachUUID: String
    ): MutualSelection =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentUUID))
                ?: throw IllegalArgumentException("学生不存在")
            val coach = CoachEntity.findById(UUID.fromString(coachUUID))
                ?: throw IllegalArgumentException("教练不存在")

            // 检查学生是否已达到最大教练数
            if (student.currentCoach >= student.maxCoach) {
                throw IllegalArgumentException("学生已达到最大可选教练数")
            }

            // 检查教练是否已达到最大学生数
            if (coach.currentStudents >= coach.maxStudents) {
                throw IllegalArgumentException("教练已达到最大可接收学生数")
            }

            // 检查是否存在相同状态的关系
            val existingRelation = MutualSelectionEntity.find {
                (MutualSelectionTable.student_id eq student.id) and
                        (MutualSelectionTable.coach_id eq coach.id) and
                        (MutualSelectionTable.status inList listOf(
                            MutualSelectionStatus.PENDING,
                            MutualSelectionStatus.APPROVED,
                            MutualSelectionStatus.ACTIVE
                        ))
            }.firstOrNull()

            if (existingRelation != null) {
                throw IllegalArgumentException("已存在相同状态的申请或关系")
            }

            // 创建新的活跃关系
            val relation = MutualSelectionEntity.new {
                this.studentID = student
                this.coachID = coach
                this.status = MutualSelectionStatus.ACTIVE
                this.applicationTime = Clock.System.now()
                this.expectedStartTime = Clock.System.now()
                this.actualStartTime = Clock.System.now()
            }

            // 更新计数
            student.currentCoach += 1
            coach.currentStudents += 1

            MUTUAL_SELECTION_LOGGER.info("管理员直接建立关系完成")

            relation.expose()
        }

    /**
     * 获取所有关系记录（管理员使用）
     */
    fun getAllRelations(
        status: String?,
        page: Int,
        size: Int
    ): List<MutualSelection> =
        transaction(database) {
            val query = if (status != null) {
                MutualSelectionEntity.find {
                    MutualSelectionTable.status eq MutualSelectionStatus.valueOf(status.uppercase())
                }
            } else {
                MutualSelectionEntity.all()
            }

            // 计算偏移量
            val offset = (page - 1) * size

            // 查询分页数据
            query
                .sortedByDescending { it.applicationTime }
                .drop(offset)
                .take(size)
                .toList()
                .expose()
        }
    
    /**
     * 获取学生当前已建立关系的教练列表
     */
    fun getStudentCurrentCoaches(
        studentUUID: String
    ): List<MutualSelection> =
        transaction(database) {
            val student = StudentEntity.findById(UUID.fromString(studentUUID))
                ?: throw IllegalArgumentException("学生不存在")

            // 查询学生所有已批准或活跃的关系
            val query = MutualSelectionEntity.find {
                (MutualSelectionTable.student_id eq student.id) and
                        (MutualSelectionTable.status inList listOf(
                            MutualSelectionStatus.APPROVED,
                            MutualSelectionStatus.ACTIVE
                        ))
            }

            query.toList().expose()
        }
    
    /**
     * 获取教练当前已建立关系的学生列表
     */
    fun getCoachCurrentStudents(
        coachUUID: String
    ): List<MutualSelection> =
        transaction(database) {
            val coach = CoachEntity.findById(UUID.fromString(coachUUID))
                ?: throw IllegalArgumentException("教练不存在")

            // 查询教练所有已批准或活跃的关系
            val query = MutualSelectionEntity.find {
                (MutualSelectionTable.coach_id eq coach.id) and
                        (MutualSelectionTable.status inList listOf(
                            MutualSelectionStatus.APPROVED,
                            MutualSelectionStatus.ACTIVE
                        ))
            }

            query.toList().expose()
        }
    
    /**
     * 获取教练历史学生列表（包括已结束关系的学生）
     */
    fun getCoachHistoricalStudents(
        coachUUID: String
    ): List<MutualSelection> =
        transaction(database) {
            val coach = CoachEntity.findById(UUID.fromString(coachUUID))
                ?: throw IllegalArgumentException("教练不存在")

            // 查询教练所有已结束的关系（包括被拒绝和非活跃的）
            val query = MutualSelectionEntity.find {
                (MutualSelectionTable.coach_id eq coach.id) and
                        (MutualSelectionTable.status inList listOf(
                            MutualSelectionStatus.REJECTED,
                            MutualSelectionStatus.INACTIVE
                        ))
            }

            query.toList().expose()
        }
}