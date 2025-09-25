@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.competition.Competition
import io.github.shaoyuanyu.ttts.dto.competition.CompetitionArrangement
import io.github.shaoyuanyu.ttts.dto.competition.CompetitionSignup
import io.github.shaoyuanyu.ttts.exceptions.NotFoundException
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import io.github.shaoyuanyu.ttts.persistence.competition.*
import io.github.shaoyuanyu.ttts.persistence.table.TableEntity
import io.github.shaoyuanyu.ttts.persistence.table.TableTable
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.plugins.LOGGER
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.*
import kotlin.math.min
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
            this.status = "未开始"
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
            }.toList().expose().map {
                it.currentSignupStudentNumber = CompetitionSignupEntity.find { CompetitionSignupTable.competition eq UUID.fromString(it.id) }.count().toInt()
                it
            }
        }

    /**
     * 查询所有比赛信息
     *
     * @return 包含所有比赛信息的列表
     */
    fun queryAllCompetitions(): List<Competition> =
        transaction(database) {
            CompetitionEntity.all().toList().expose().map {
                it.currentSignupStudentNumber = CompetitionSignupEntity.find { CompetitionSignupTable.competition eq UUID.fromString(it.id) }.count().toInt()
                it
            }
        }

    /**
     * 根据比赛ID查询比赛信息
     */
    fun queryCompetitionById(competitionId: String): Competition =
        transaction(database) {
            val competition = CompetitionEntity.findById(UUID.fromString(competitionId))
                ?: throw NotFoundException("比赛不存在")
            competition.expose()
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

            if (competition.status != "未开始") {
                throw IllegalStateException("比赛已开始，不能再报名")
            }

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
     * 安排比赛
     */
    fun arrangeCompetition(competitionId: String) {
        transaction(database) {
            val competition = CompetitionEntity.findById(UUID.fromString(competitionId))
                ?: throw NotFoundException("比赛不存在")

            if (competition.status != "未开始") {
                throw IllegalStateException("比赛已开始，不能再安排")
            }

            // 获取该比赛的所有报名信息，按组别分组
            val signups = CompetitionSignupEntity.find {
                CompetitionSignupTable.competition eq competition.id
            }.toList()

            // 按组别分组
            val groupedSignups = signups.groupBy { it.group }

            // 为每个组别安排比赛
            for ((group, participants) in groupedSignups) {
                if (participants.size <= 6) {
                    // 少于等于6人，采用全循环方式
                    arrangeRoundRobin(competition, group, participants)
                } else {
                    // 多于6人，分小组全循环，再交叉淘汰
                    arrangeGroupStageAndKnockout(competition, group, participants)
                }
            }

            competition.status = "进行中"
        }
    }
    
    /**
     * 全循环排赛算法
     */
    private fun arrangeRoundRobin(
        competition: CompetitionEntity,
        group: String,
        participants: List<CompetitionSignupEntity>
    ) {
        transaction(database) {
            if (participants.isEmpty()) throw IllegalArgumentException("没有报名的玩家")

            val players = participants.map { it.user }
            val n = players.size

            // 获取该校区的球台
            val tables = TableEntity.find { TableTable.campus eq competition.campus.id }.toList()
            val tableCount = tables.size

            if (tableCount == 0) throw NotFoundException("该校区没有球台")

            // 使用标准的循环赛算法（轮转法）
            val workingPlayers = if (n % 2 == 1) n + 1 else n
            val rounds = workingPlayers - 1
            val halfSize = workingPlayers / 2

            // 为确保不出现自己和自己比赛，我们预先生成所有可能的配对
            val allMatches = mutableListOf<Pair<Int, Int>>()
            
            // 生成所有不重复的配对组合
            for (i in 0 until n) {
                for (j in i + 1 until n) {
                    allMatches.add(Pair(i, j))
                }
            }
            
            // 按轮次分配比赛
            val matchesPerRound = minOf(halfSize, tableCount)
            val totalMatches = allMatches.size
            
            for (round in 1..rounds) {
                val matches = mutableListOf<Pair<Int, Int>>()
                
                // 从剩余比赛中选择本轮比赛
                val startIndex = (round - 1) * matchesPerRound
                val endIndex = minOf(startIndex + matchesPerRound, totalMatches)
                
                if (startIndex < totalMatches) {
                    for (i in startIndex until minOf(endIndex, totalMatches)) {
                        if (i < allMatches.size) {
                            matches.add(allMatches[i])
                        }
                    }
                }

                // 为每场比赛分配球台
                for ((matchIndex, match) in matches.withIndex()) {
                    if (matchIndex >= tableCount) break // 球台不够

                    val table = tables[matchIndex]

                    // 确保两个选手都不是null且不是同一个人
                    if (match.first < n && 
                        match.second < n &&
                        match.first != match.second) {
                        
                        val playerA = players[match.first]
                        val playerB = players[match.second]
                        
                        LOGGER.info("安排比赛：${competition.name} 第${round}轮 ${table.indexInCampus}号球台 ${playerA.realName} VS ${playerB.realName}")

                        // 创建比赛安排记录
                        CompetitionArrangementEntity.new {
                            this.competition = competition
                            this.turnNumber = round
                            this.table = table
                            this.playerA = playerA
                            this.playerB = playerB
                            this.status = "SCHEDULED"
                            this.result = ""
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 分组循环+交叉淘汰算法
     */
    private fun arrangeGroupStageAndKnockout(
        competition: CompetitionEntity,
        group: String,
        participants: List<CompetitionSignupEntity>
    ) {
        // TODO: 实现分组循环+交叉淘汰算法
        // 这里简化处理，仍使用全循环算法
        arrangeRoundRobin(competition, group, participants)
    }

    /**
     * 获取用户在某场比赛中的赛程安排
     */
    fun getUserCompetitionSchedule(userId: String, competitionId: String): List<CompetitionArrangement> =
        transaction(database) {
            // 验证用户是否存在
            val user = UserEntity.findById(UUID.fromString(userId))
                ?: throw NotFoundException("用户不存在")
            
            // 验证比赛是否存在
            val competition = CompetitionEntity.findById(UUID.fromString(competitionId))
                ?: throw NotFoundException("比赛不存在")
            
            // 查询该用户在该比赛中的所有赛程安排
            CompetitionArrangementEntity.find {
                (CompetitionArrangementTable.competition eq competition.id) and (
                    (CompetitionArrangementTable.playerA eq user.id) or 
                    (CompetitionArrangementTable.playerB eq user.id)
                )
            }.toList().expose()
        }
}