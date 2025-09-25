package io.github.shaoyuanyu.ttts.persistence.competition

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.date

object CompetitionTable : UUIDTable("competition") {
    // 比赛名称
    val name: Column<String> = varchar("name", 255)
    
    // 比赛类型（自定义）
    val type: Column<String> = varchar("type", 50)

    // 比赛所属校区
    val campusId: Column<Int> = integer("campus_id")
    
    // 比赛日期
    val date: Column<LocalDate> = date("date")
    
    // 报名截止日期
    val registrationDeadline: Column<LocalDate> = date("registration_deadline")
    
    // 报名费用
    val fee: Column<Float> = float("fee")
    
    // 比赛描述
    val description: Column<String> = text("description")
}