@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.student_coach

import io.github.shaoyuanyu.ttts.persistence.coach.CoachTable
import io.github.shaoyuanyu.ttts.persistence.student.StudentTable
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object StudentCoachRelationTable : UUIDTable("student_coach") {
    // 教练引用
    val coach_id = reference("coach_id", CoachTable, ReferenceOption.CASCADE)
    
    // 学生引用
    val student_id = reference("student_id", StudentTable, ReferenceOption.CASCADE)
    
    // 申请/关系状态: pending(待处理), approved(已批准), rejected(已拒绝), active(活跃), inactive(非活跃)
    val status: Column<String> = varchar("status", 32).default("pending")
    
    // 申请时间
    val application_time: Column<Instant> = timestamp("application_time")
    
    // 预期开始时间
    val expected_start_time: Column<Instant> = timestamp("expected_start_time")
    
    // 实际开始时间（批准时设置）
    val actual_start_time: Column<Instant?> = timestamp("actual_start_time").nullable()
    
    // 结束时间
    val end_time: Column<Instant?> = timestamp("end_time").nullable()
    
    // 创建时间
    val created_at: Column<Instant> = timestamp("created_at")
    
    // 最后更新时间
    val updated_at: Column<Instant> = timestamp("updated_at")
    
    // 学生留言
    val student_message: Column<String?> = varchar("student_message", 512).nullable()
    
    // 教练回复
    val coach_message: Column<String?> = varchar("coach_message", 512).nullable()
    
    // 索引定义
    init {
        index(true, coach_id, status)
        index(true, student_id, status)
        index(false, application_time)
        index(false, created_at)
    }
}