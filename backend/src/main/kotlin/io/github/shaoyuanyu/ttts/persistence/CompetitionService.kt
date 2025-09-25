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
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.or
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
     * 安排比赛
     */
    fun arrangeCompetition(competitionId: String) {
        transaction(database) {
            val competition = CompetitionEntity.findById(UUID.fromString(competitionId))
                ?: throw NotFoundException("比赛不存在")

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
        if (participants.isEmpty()) return
        
        val players = participants.map { it.user }
        val n = players.size
        
        // 获取该校区的球台
        val tables = TableEntity.find { TableTable.campus eq competition.campus.id }.toList()
        val tableCount = tables.size
        
        if (tableCount == 0) return  // 没有球台无法安排
        
        if (n % 2 == 1) {
            // 奇数人情况：添加一个虚拟的轮空选手
            val totalPlayers = n + 1
            val rounds = totalPlayers - 1
            
            for (round in 1..rounds) {
                val matches = mutableListOf<Pair<Int?, Int?>>()
                
                // 使用标准循环赛算法
                for (i in 1..totalPlayers / 2) {
                    val player1Pos = if (i == 1) 1 else (round + i - 3) % (totalPlayers - 1) + 1
                    val player2Pos = if (i == 1) (round - 1) % (totalPlayers - 1) + 1 
                                    else (round - i - 1 + totalPlayers - 1) % (totalPlayers - 1) + 1
                    
                    // 处理轮空情况
                    val p1Index = if (player1Pos <= n) player1Pos - 1 else null  // 转换为0基索引
                    val p2Index = if (player2Pos <= n) player2Pos - 1 else null  // 转换为0基索引
                    
                    if (p1Index != null && p2Index != null) {
                        matches.add(Pair(p1Index, p2Index))
                    } else if (p1Index != null) {
                        // p1轮空轮
                    } else if (p2Index != null) {
                        // p2轮空轮
                    }
                }
                
                // 为每场比赛分配球台
                for ((matchIndex, match) in matches.withIndex()) {
                    if (matchIndex >= tableCount) break // 球台不够
                    
                    val table = tables[matchIndex]
                    
                    // 只有当两个选手都存在时才创建比赛
                    if (match.first != null && match.second != null) {
                        // 创建比赛安排记录
                        CompetitionArrangementEntity.new {
                            this.competition = competition
                            this.turnNumber = round
                            this.table = table
                            this.playerA = players[match.first!!]
                            this.playerB = players[match.second!!]
                            this.status = "SCHEDULED"
                            this.result = ""
                        }
                    }
                }
            }
        } else {
            // 偶数人情况
            val rounds = n - 1
            
            for (round in 1..rounds) {
                val matches = mutableListOf<Pair<Int, Int>>()
                
                // 使用标准循环赛算法
                for (i in 1..n / 2) {
                    val player1Pos = if (i == 1) 1 else (round + i - 3) % (n - 1) + 1
                    val player2Pos = if (i == 1) (round + n - 3) % (n - 1) + 1
                                    else (round - i - 1 + n - 1) % (n - 1) + 1
                    
                    // 转换为0基索引
                    val p1Index = player1Pos - 1
                    val p2Index = player2Pos - 1
                    
                    if (p1Index < n && p2Index < n && p1Index != p2Index) {
                        matches.add(Pair(p1Index, p2Index))
                    }
                }
                
                // 为每场比赛分配球台
                for ((matchIndex, match) in matches.withIndex()) {
                    if (matchIndex >= tableCount) break // 球台不够
                    
                    val table = tables[matchIndex]
                    
                    // 创建比赛安排记录
                    CompetitionArrangementEntity.new {
                        this.competition = competition
                        this.turnNumber = round
                        this.table = table
                        this.playerA = players[match.first]
                        this.playerB = players[match.second]
                        this.status = "SCHEDULED"
                        this.result = ""
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