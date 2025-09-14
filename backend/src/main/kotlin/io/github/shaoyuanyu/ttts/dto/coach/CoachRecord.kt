package io.github.shaoyuanyu.ttts.dto.coach

import kotlinx.serialization.Serializable

@Serializable
data class CoachRecord(
    val coachId: String,
    var username: String,
    var realName: String,
    var gender: String,
    var age : Int,
    var photoUrl : String,
    var achievements : String,
    var level : String,
    var hourlyRate : Float,
    var currentStudents : Int,
)