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

            studentService.deduct(userId, competition.fee)
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
                arrangeRoundRobin(competition, group, participants)
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
            if (participants.isEmpty()) return@transaction
            
            // 获取该校区的所有球台
            val tables = TableEntity.find { TableTable.campus eq competition.campus.id }.toList()
            if (tables.isEmpty()) throw IllegalStateException("该校区没有可用球台")
            
            val players = participants.map { it.user }
            val n = players.size
            
            // 创建循环赛对阵表
            val rounds = mutableListOf<List<Pair<Int, Int>>>()
            
            if (n % 2 == 0) {
                // 偶数人情况
                evenRoundRobin(n, rounds)
            } else {
                // 奇数人情况，添加一个轮空位（用-1表示）
                oddRoundRobin(n, rounds)
            }
            
            // 为每一轮创建比赛安排
            for ((roundIndex, round) in rounds.withIndex()) {
                val turnNumber = roundIndex + 1
                // 循环使用球台
                val table = tables[roundIndex % tables.size]
                
                for ((playerAIndex, playerBIndex) in round) {
                    // 如果有轮空(-1)，则跳过这场比赛
                    if (playerAIndex == -1 || playerBIndex == -1) continue
                    
                    CompetitionArrangementEntity.new {
                        this.competition = competition
                        this.turnNumber = turnNumber
                        this.table = table
                        this.playerA = players[playerAIndex]
                        this.playerB = players[playerBIndex]
                        this.status = "未开始"
                        this.result = ""
                        this.winner = 0
                    }
                }
            }
        }
    }
    
    /**
     * 偶数人循环赛算法
     */
    private fun evenRoundRobin(n: Int, rounds: MutableList<List<Pair<Int, Int>>>) {
        val players = (0 until n).toMutableList()
        
        // 进行 n-1 轮比赛
        for (round in 0 until n - 1) {
            val matches = mutableListOf<Pair<Int, Int>>()
            
            // 第一个选手固定，其他选手顺时针轮转
            matches.add(Pair(players[0], players[n - 1]))
            
            // 其余配对
            for (i in 1 until n / 2) {
                val left = players[i]
                val right = players[n - 1 - i]
                matches.add(Pair(left, right))
            }
            
            rounds.add(matches)
            
            // 轮转选手（除第一个外）
            val temp = players[n - 1]
            for (i in n - 1 downTo 2) {
                players[i] = players[i - 1]
            }
            players[1] = temp
        }
    }
    
    /**
     * 奇数人循环赛算法
     */
    private fun oddRoundRobin(n: Int, rounds: MutableList<List<Pair<Int, Int>>>) {
        // 添加一个轮空位，使其变为偶数
        val players = (0 until n).toMutableList()
        players.add(-1) // -1 表示轮空
        
        val totalPlayers = players.size
        
        // 进行 totalPlayers-1 轮比赛
        for (round in 0 until totalPlayers - 1) {
            val matches = mutableListOf<Pair<Int, Int>>()
            
            // 第一个选手固定，其他选手顺时针轮转
            matches.add(Pair(players[0], players[totalPlayers - 1]))
            
            // 其余配对
            for (i in 1 until totalPlayers / 2) {
                val left = players[i]
                val right = players[totalPlayers - 1 - i]
                matches.add(Pair(left, right))
            }
            
            rounds.add(matches)
            
            // 轮转选手（除第一个外）
            val temp = players[totalPlayers - 1]
            for (i in totalPlayers - 1 downTo 2) {
                players[i] = players[i - 1]
            }
            players[1] = temp
        }
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