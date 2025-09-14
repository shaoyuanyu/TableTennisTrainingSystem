package io.github.shaoyuanyu.ttts.dto.course

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: String,
    val title: String,
    val description: String? = null,
    val type: String, // individual or group
    val level: String, // beginner, intermediate, advanced
    val date: String, // YYYY-MM-DD
    val startTime: String, // HH:MM
    val endTime: String, // HH:MM
    val duration: Int, // minutes
    val location: String,
    val status: String, // scheduled, confirmed, completed, cancelled
    val maxStudents: Int? = null,
    val currentStudents: Int,
    val price: Int, // cents
    val coachId: String, // UUID
    val coachName: String,
    val campusId: Int,
    val campusName: String,
    val createdAt: String, // ISO datetime
    val updatedAt: String  // ISO datetime
)