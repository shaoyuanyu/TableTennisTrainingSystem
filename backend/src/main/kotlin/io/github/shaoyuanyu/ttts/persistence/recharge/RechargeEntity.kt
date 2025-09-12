@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence.recharge

import io.github.shaoyuanyu.ttts.dto.recharge.RechargeRecord
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.IntEntity
import org.jetbrains.exposed.v1.dao.IntEntityClass


import kotlin.time.ExperimentalTime

class RechargeEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<RechargeEntity>(RechargeTable)
    var userId by RechargeTable.userId
    var amount by RechargeTable.amount
    var createdAt by RechargeTable.createdAt
}
fun RechargeEntity.expose()= RechargeRecord(
    userId =userId,
    amount =amount,
    createdAt =createdAt,
)

