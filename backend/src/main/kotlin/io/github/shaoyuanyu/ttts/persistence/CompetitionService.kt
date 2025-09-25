@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.competition.Competition
import io.github.shaoyuanyu.ttts.dto.competition.CompetitionSignup
import io.github.shaoyuanyu.ttts.exceptions.NotFoundException
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import io.github.shaoyuanyu.ttts.persistence.competition.*
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CompetitionService(
    private val database: Database,
    private val studentService: StudentService,
) {
    /**
     * 创建比赛
     *
     * @param name 比赛名称
     * @param type 比赛类型
     * @param date 比赛日期
     * @param registrationDeadline 报名截止日期
     * @param fee 报名费用
     * @param description 比赛描述
     * @return 创建的比赛ID
     */
    fun createCompetition(
        name: String,
        type: String,
        campusId: Int,
        date: LocalDate,
        registrationDeadline: LocalDate,
        fee: Float,
        description: String
    ) = transaction(database) {
        // 参数验证
        if (name.isBlank()) {
            throw IllegalArgumentException("比赛名称不能为空")
        }

        if (type.isBlank()) {
            throw IllegalArgumentException("比赛类型不能为空")
        }

        if (date < Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Shanghai")).date) {
            throw IllegalArgumentException("比赛日期不能早于当前日期")
        }

        if (registrationDeadline > date) {
            throw IllegalArgumentException("报名截止日期不能晚于比赛日期")
        }

        if (fee < 0) {
            throw IllegalArgumentException("报名费用不能为负数")
        }

        // 获取校区
        val campus = CampusEntity.findById(campusId) ?: throw NotFoundException("校区不存在")

        CompetitionEntity.new {
            this.name = name
            this.type = type
            this.campus = campus
            this.date = date
            this.registrationDeadline = registrationDeadline
            this.fee = fee
            this.description = description
        }
    }


    /**
     * 根据用户ID查询同校区的比赛信息
     *
     * @param userId 用户ID
     * @return 同校区的比赛列表
     */
    fun queryCampusCompetition(userId: String): List<Competition> =
        transaction(database) {
            val campus = UserEntity.findById(UUID.fromString(userId))?.campus
                ?: throw NotFoundException("用户不存在")

            CompetitionEntity.find {
                CompetitionTable.campus eq campus.id
            }.toList().expose()
        }

    /**
     * 查询所有比赛信息
     *
     * @return 包含所有比赛信息的列表
     */
    fun queryAllCompetitions(): List<Competition> =
        transaction(database) {
            CompetitionEntity.all().toList().expose()
        }

    /**
     * 报名参加比赛
     */
    fun signupCompetition(userId: String, competitionId: String, group: String) {
        transaction(database) {
            // 验证小组名称是否有效
            val validGroups = listOf("A", "B", "C")
            if (group !in validGroups) {
                throw IllegalStateException("无效的小组名称，必须是A、B或C")
            }

            // 检查用户是否已经报名该比赛
            val existingSignup = CompetitionSignupEntity.find { 
                (CompetitionSignupTable.user eq UUID.fromString(userId)) and
                (CompetitionSignupTable.competition eq UUID.fromString(competitionId))
            }
                .limit(1)
                .singleOrNull()
            if (existingSignup != null) {
                throw IllegalStateException("用户已经报名过该比赛")
            }

            // 获取用户信息
            val user = UserEntity.findById(UUID.fromString(userId))
                ?: throw NotFoundException("用户不存在")
                
            // 获取比赛信息
            val competition = CompetitionEntity.findById(UUID.fromString(competitionId))
                ?: throw NotFoundException("比赛不存在")

            // 插入报名记录
            CompetitionSignupEntity.new {
                this.user = user
                this.competition = competition
                this.group = group
                this.status = "ACTIVE"
                this.createdAt = Clock.System.now()
            }
        }
    }

    /**
     * 查询比赛报名信息
     */
    fun queryCompetitionSignup(userId: String): List<CompetitionSignup> =
        transaction(database) {
            CompetitionSignupEntity.find { CompetitionSignupTable.user eq UUID.fromString(userId) }.toList().expose()
        }

    /**
     * 获取用户个人比赛安排
     */
    fun getUserCompetitionSchedule(userId: String) {
    }
}