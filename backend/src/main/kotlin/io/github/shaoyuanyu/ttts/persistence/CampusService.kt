@file:OptIn(ExperimentalTime::class)
package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.campus.CampusCreateRequest
import io.github.shaoyuanyu.ttts.dto.campus.CampusQueryRequest
import io.github.shaoyuanyu.ttts.dto.student.Group
import io.github.shaoyuanyu.ttts.dto.student.Status
import io.github.shaoyuanyu.ttts.dto.user.UserRole
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import io.github.shaoyuanyu.ttts.persistence.table.TableEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserEntity
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import io.github.shaoyuanyu.ttts.utils.encryptPasswd
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
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
                    tableNumber = 0
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
            // 检查是否已存在相同用户名的用户
            val existingUser = UserEntity.find { UserTable.username eq newCampus.contactPerson }.firstOrNull()
            if (existingUser != null) {
                throw Exception("用户名 '${newCampus.contactPerson}' 已存在，无法创建校区管理员")
            }

            // 创建校区
            val campusEntity = CampusEntity.new {
                campusName = newCampus.campusName
                address = newCampus.address
                contactPerson = newCampus.contactPerson
                phone = newCampus.phone
                email = newCampus.email
                balance = 0.0f
                isCentral = false
                tableNumber=0
                createdAt = Clock.System.now()
                lastLoginAt = createdAt
            }

            // 获取新创建的校区ID
            val campusId = campusEntity.id.value

            // 创建校区管理员用户
            UserEntity.new {
                username = newCampus.username
                realName = newCampus.contactPerson
                encryptedPassword = encryptPasswd("12345678") // 加密密码
                gender = ""
                age = 0
                phoneNumber = newCampus.phone
                email = newCampus.email
                this.campusId = campusId // 关联校区ID
                role = UserRole.CAMPUS_ADMIN
                status = "ACTIVE"
                this.createdAt = Clock.System.now()
                this.lastLoginAt = this.createdAt
            }
        }.also {
            USER_LOGGER.info("创建校区成功，校区名：${newCampus.campusName}，管理员：${newCampus.contactPerson}")
        }
    }
    /**
     * 查询所有校区
     */
    fun getAllCampusNames(page: Int,size: Int):Pair<List<CampusQueryRequest>,Int> =
        transaction(database) {
            val query = CampusEntity.all().toList()

            val offset = (page-1) * size
            val total = query.size
            val records = query
                .sortedBy { it.createdAt }
                .drop(offset)
                .take(size)
                .map {CampusQueryRequest(
                    id = it.id.value,
                    campusName = it.campusName,
                )}

            records to total
        }
    /**
     * 增加球桌数量
     */
    fun addTable(userId: String, number: Int) =
        transaction(database) {
            val userEntity = UserEntity.findById(UUID.fromString(userId))
                ?: throw IllegalArgumentException("用户不存在")

            val campusId = userEntity.campusId
            val campusEntity = CampusEntity.findById(campusId)
                ?: throw IllegalArgumentException("校区不存在")

            // 获取当前的球桌数量作为起始索引
            var currentTableNumber = campusEntity.tableNumber

            // 循环添加指定数量的球桌
            repeat(number) {
                currentTableNumber += 1

                TableEntity.new {
                    this.status = Status.free
                    this.group = Group.free
                    this.indexInCampus = currentTableNumber
                    this.campusId = campusId
                }
            }

            // 更新校区的球桌总数
            campusEntity.tableNumber = currentTableNumber

        }.also {
            USER_LOGGER.info("增加球桌成功，用户ID：$userId，增加数量：$number")
        }
}