@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.dto.campus

import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
@Serializable
data class CampusCreateRequest(
    var campusName : String,
    var address : String,
    var contactPerson : String,
    var phone : String,
    var email : String,
)
@Serializable
data class CampusqueryRequest(
    val campusName : String,
)