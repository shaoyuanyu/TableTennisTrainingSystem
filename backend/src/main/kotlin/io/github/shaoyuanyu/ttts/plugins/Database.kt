package io.github.shaoyuanyu.ttts.plugins

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.shaoyuanyu.ttts.persistence.campus.CampusTable
import io.github.shaoyuanyu.ttts.persistence.coach.CoachTable
import io.github.shaoyuanyu.ttts.persistence.competition.CompetitionSignupTable
import io.github.shaoyuanyu.ttts.persistence.competition.CompetitionTable
import io.github.shaoyuanyu.ttts.persistence.message.MessageTable
import io.github.shaoyuanyu.ttts.persistence.mutual_selection.MutualSelectionTable
import io.github.shaoyuanyu.ttts.persistence.recharge.RechargeTable
import io.github.shaoyuanyu.ttts.persistence.student.StudentTable
import io.github.shaoyuanyu.ttts.persistence.table.TableTable
import io.github.shaoyuanyu.ttts.persistence.course.CourseTable
import io.github.shaoyuanyu.ttts.persistence.user.UserTable
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun configureDatabase(
    url: String,
    driver: String,
    user: String,
    password: String
): Database {
    // 配置HiKari
    val dataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = user
            this.password = password
            maximumPoolSize = 24
            isReadOnly = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
        }
    )

    // 创建database实例
    val database = Database.connect(datasource = dataSource)

    // 第一次transaction与数据库建立连接
    // 检查table并创建缺失的，DSL "CREATE TABLE IF NOT EXISTS"
    transaction(database) {
        SchemaUtils.create(UserTable)
        SchemaUtils.create(CoachTable)
        SchemaUtils.create(StudentTable)
        SchemaUtils.create(MutualSelectionTable)
        SchemaUtils.create(CampusTable)
        SchemaUtils.create(MessageTable)
        SchemaUtils.create(RechargeTable)
        SchemaUtils.create(TableTable)
        SchemaUtils.create(CourseTable)
        SchemaUtils.create(CompetitionTable)
        SchemaUtils.create(CompetitionSignupTable)
    }

    return database
}