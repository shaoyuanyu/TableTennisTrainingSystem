@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.campus.CampusCreateRequest
import io.github.shaoyuanyu.ttts.dto.campus.CampusqueryRequest
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CampusService(
    private val database: Database) {
    init {
         //* 若 campus表为空自动创建中心校区
        transaction(database) {
            if (CampusEntity.all().empty()) {
                CampusEntity.new {
                    campusName = "中心校区"
                    address = "吉林省长春市朝阳区"
                    contactPerson = "super_admin"
                    phone = "12345678901"
                    email = "jlu@emials.com"
                    balance = 0.0f
                    isCentral = true
                    createdAt = Clock.System.now()
                    lastLoginAt = createdAt
                }
            }
        }
    }

    /**
     * 创建校区
     */
    fun createCampus(newCampus: CampusCreateRequest) {
        transaction(database) {
            // 检查校区名是否已存在::已改为数据库表实现
//            val existingCampus = CampusEntity.find { CampusTable.campus_name eq newCampus.campusName }.firstOrNull()
//
//            if (existingCampus != null) {
//                throw Exception("校区名 '${newCampus.campusName}' 已存在，创建失败")
//            }

            CampusEntity.new {
                campusName = newCampus.campusName
                address = newCampus.address
                contactPerson = newCampus.contactPerson
                phone = newCampus.phone
                email = newCampus.email
                balance = 0.0f
                isCentral = false
                createdAt = Clock.System.now()
                lastLoginAt = createdAt
            }
        }.also {
            LOGGER.info("创建校区成功，校区名：${newCampus.campusName}")
        }
    }
    /**
     * 查询所有校区
     */
    fun getAllCampusNames(page: Int,size: Int):Pair<List<CampusqueryRequest>,Int> =
        transaction(database) {
            val query = CampusEntity.all().toList()

            val offset = (page-1) * size
            val total = query.size
            val records = query
                .sortedBy { it.createdAt }
                .drop(offset)
                .take(size)
                .map {CampusqueryRequest(
                    id = it.id.value,
                    campusName = it.campusName,
                )}

            records to total
        }
}