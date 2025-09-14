@file:OptIn(ExperimentalTime::class)

package io.github.shaoyuanyu.ttts.persistence

import io.github.shaoyuanyu.ttts.dto.course.*
import io.github.shaoyuanyu.ttts.dto.student.Status
import io.github.shaoyuanyu.ttts.exceptions.BadRequestException
import io.github.shaoyuanyu.ttts.exceptions.NotFoundException
import io.github.shaoyuanyu.ttts.persistence.campus.CampusEntity
import io.github.shaoyuanyu.ttts.persistence.coach.CoachEntity
import io.github.shaoyuanyu.ttts.persistence.course.CourseEntity
import io.github.shaoyuanyu.ttts.persistence.course.CourseTable
import io.github.shaoyuanyu.ttts.persistence.course.expose
import io.github.shaoyuanyu.ttts.persistence.student.StudentEntity
import io.github.shaoyuanyu.ttts.persistence.table.TableEntity
import io.github.shaoyuanyu.ttts.persistence.table.TableTable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal val COURSE_SERVICE_LOGGER = LoggerFactory.getLogger("io.github.shaoyuanyu.ttts.persistence.course")

class CourseService(
    private val database: Database
) {
    
    /**
     * 创建课程
     */
    fun createCourse(request: CourseCreateRequest): Course {
        return transaction(database) {
            // 验证输入数据
            validateCourseRequest(request)
            
            // 验证学生和教练是否存在
            val student = StudentEntity.findById(UUID.fromString(request.studentId))
                ?: throw NotFoundException("学生不存在")
            val coach = CoachEntity.findById(UUID.fromString(request.coachId))
                ?: throw NotFoundException("教练不存在")
            val campus = CampusEntity.findById(request.campusId)
                ?: throw NotFoundException("校区不存在")
            
            // 检查时间冲突
            checkTimeConflict(
                UUID.fromString(request.coachId),
                UUID.fromString(request.studentId),
                LocalDate.parse(request.date),
                LocalTime.parse(request.startTime),
                LocalTime.parse(request.endTime)
            )
            
            // 分配球桌
            val assignedTable = assignTable(request, campus.id.value)
            
            // 解析时间
            val parsedDate = LocalDate.parse(request.date)
            val parsedStartTime = LocalTime.parse(request.startTime)
            val parsedEndTime = LocalTime.parse(request.endTime)
            
            // 创建课程
            val course = CourseEntity.new {
                title = request.title
                description = request.description
                date = parsedDate
                startTime = parsedStartTime
                endTime = parsedEndTime
                location = request.location
                status = CourseStatus.PENDING
                price = request.price
                coachId = coach.id.value
                coachName = coach.userId.realName
                studentId = student.id.value
                studentName = student.userId.realName
                campusId = campus.id.value
                campusName = campus.campusName
                tableId = assignedTable
                lessonContent = null
                paymentStatus = "PENDING"
                attendanceStatus = null
                studentFeedback = null
                studentRating = null
                createdAt = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            }
            
            COURSE_SERVICE_LOGGER.info("创建课程成功: ${course.id}")
            course.expose()
        }
    }
    
    /**
     * 查询课程列表（通用查询）
     */
    fun queryCourses(request: QueryCourseRequest): CourseScheduleResponse {
        return transaction(database) {
            val query = CourseEntity.all()
            
            // 过滤条件
            val filteredQuery = query.toList().filter { course ->
                (request.status == null || course.status == CourseStatus.valueOf(request.status)) &&
                (request.studentId == null || course.studentId == UUID.fromString(request.studentId)) &&
                (request.coachId == null || course.coachId == UUID.fromString(request.coachId)) &&
                (request.campusId == null || course.campusId == request.campusId) &&
                (request.dateFrom == null || course.date >= LocalDate.parse(request.dateFrom)) &&
                (request.dateTo == null || course.date <= LocalDate.parse(request.dateTo))
            }
            
            // 排序
            val sortedCourses = filteredQuery.sortedWith(
                compareBy<CourseEntity> { it.date }.thenBy { it.startTime }
            )
            
            // 分页
            val total = sortedCourses.size
            val offset = (request.page - 1) * request.size
            val pagedCourses = sortedCourses.drop(offset).take(request.size)
            
            CourseScheduleResponse(
                courses = pagedCourses.map { it.expose() },
                total = total,
                page = request.page,
                size = request.size
            )
        }
    }
    
    /**
     * 学生查询自己的课表
     */
    fun getStudentSchedule(studentId: String, dateFrom: String?, dateTo: String?): List<Course> =
        transaction(database) {
            val query = CourseEntity.find { CourseTable.studentId eq UUID.fromString(studentId) }

            val filteredCourses = query.toList().filter { course ->
                (dateFrom == null || course.date >= LocalDate.parse(dateFrom)) &&
                        (dateTo == null || course.date <= LocalDate.parse(dateTo))
            }

            filteredCourses.sortedWith(
                compareBy<CourseEntity> { it.date }.thenBy { it.startTime }
            ).map { it.expose() }
        }
    
    /**
     * 学生查询指定教练的课表
     */
    fun getCoachScheduleForStudent(coachId: String, dateFrom: String?, dateTo: String?): List<Course> =
        transaction(database) {
            val query = CourseEntity.find { CourseTable.coachId eq UUID.fromString(coachId) }

            val filteredCourses = query.toList().filter { course ->
                (dateFrom == null || course.date >= LocalDate.parse(dateFrom)) &&
                        (dateTo == null || course.date <= LocalDate.parse(dateTo))
            }

            filteredCourses.sortedWith(
                compareBy<CourseEntity> { it.date }.thenBy { it.startTime }
            ).map { it.expose() }
        }
    
    /**
     * 教练查询自己的课表
     */
    fun getCoachSchedule(coachId: String, dateFrom: String?, dateTo: String?): List<Course> =
        transaction(database) {
            val query = CourseEntity.find { CourseTable.coachId eq UUID.fromString(coachId) }

            val filteredCourses = query.toList().filter { course ->
                (dateFrom == null || course.date >= LocalDate.parse(dateFrom)) &&
                        (dateTo == null || course.date <= LocalDate.parse(dateTo))
            }

            filteredCourses.sortedWith(
                compareBy<CourseEntity> { it.date }.thenBy { it.startTime }
            ).map { it.expose() }
        }
    
    /**
     * 更新课程状态
     */
    fun updateCourseStatus(courseId: String, status: CourseStatus): Course =
        transaction(database) {
            val course = CourseEntity.findById(UUID.fromString(courseId))
                ?: throw NotFoundException("课程不存在")

            course.status = status
            course.expose()
        }
    
    /**
     * 教练填写课程内容
     */
    fun updateLessonContent(request: LessonContentUpdateRequest): Course =
        transaction(database) {
            val course = CourseEntity.findById(UUID.fromString(request.courseId))
                ?: throw NotFoundException("课程不存在")

            course.lessonContent = request.lessonContent
            course.expose()
        }
    
    /**
     * 学生提交反馈
     */
    fun submitFeedback(request: StudentFeedbackRequest): Course =
        transaction(database) {
            val course = CourseEntity.findById(UUID.fromString(request.courseId))
                ?: throw NotFoundException("课程不存在")

            course.studentFeedback = request.studentFeedback
            course.studentRating = request.studentRating
            course.attendanceStatus = request.attendanceStatus
            course.expose()
        }
    
    /**
     * 验证课程请求数据
     */
    private fun validateCourseRequest(request: CourseCreateRequest) {
        try {
            val date = LocalDate.parse(request.date)
            val startTime = LocalTime.parse(request.startTime)
            val endTime = LocalTime.parse(request.endTime)
            
            if (startTime >= endTime) {
                throw BadRequestException("开始时间必须早于结束时间")
            }
            
            // 检查是否是未来时间
            val now = Clock.System.now().toLocalDateTime(TimeZone.of("Asia/Shanghai"))
            if (date < now.date || (date == now.date && startTime <= now.time)) {
                throw BadRequestException("课程时间必须是未来时间")
            }
            
        } catch (_: Exception) {
            throw BadRequestException("日期或时间格式不正确")
        }
    }
    
    /**
     * 检查时间冲突
     */
    private fun checkTimeConflict(
        coachId: UUID, 
        studentId: UUID, 
        date: LocalDate, 
        startTime: LocalTime, 
        endTime: LocalTime,
        excludeCourseId: UUID? = null
    ) {
        // 检查教练时间冲突
        val coachConflictQuery = CourseEntity.find { 
            CourseTable.coachId eq coachId and
            (CourseTable.date eq date) and
            (CourseTable.status neq CourseStatus.CANCELLED)
        }
        
        val coachConflict = coachConflictQuery.find { course ->
            (excludeCourseId == null || course.id.value != excludeCourseId) &&
            (
                (course.startTime <= startTime && course.endTime > startTime) ||
                (course.startTime < endTime && course.endTime >= endTime) ||
                (course.startTime >= startTime && course.endTime <= endTime)
            )
        }
        
        if (coachConflict != null) {
            throw BadRequestException("教练在该时间段已有课程安排")
        }
        
        // 检查学生时间冲突
        val studentConflictQuery = CourseEntity.find { 
            CourseTable.studentId eq studentId and
            (CourseTable.date eq date) and
            (CourseTable.status neq CourseStatus.CANCELLED)
        }
        
        val studentConflict = studentConflictQuery.find { course ->
            (excludeCourseId == null || course.id.value != excludeCourseId) &&
            (
                (course.startTime <= startTime && course.endTime > startTime) ||
                (course.startTime < endTime && course.endTime >= endTime) ||
                (course.startTime >= startTime && course.endTime <= endTime)
            )
        }
        
        if (studentConflict != null) {
            throw BadRequestException("学生在该时间段已有课程安排")
        }
    }
    
    /**
     * 分配球桌
     * @param request 课程创建请求
     * @param campusId 校区ID
     * @return Pair<分配的球桌实体（可为空）, 球桌编号（可为空）>
     */
    private fun assignTable(request: CourseCreateRequest, campusId: Int): TableEntity {
        // 如果请求中指定了球桌，优先使用指定的球桌
        if (request.tableId != null) {
            val specifiedTable = TableEntity.findById(UUID.fromString(request.tableId))
            if (specifiedTable != null && specifiedTable.campusId == campusId) {
                // 检查球桌在课程时间段是否可用
                if (isTableAvailable(specifiedTable, LocalDate.parse(request.date), 
                    LocalTime.parse(request.startTime), LocalTime.parse(request.endTime))) {
                    return specifiedTable
                } else {
                    throw BadRequestException("指定的球桌在该时间段不可用")
                }
            } else {
                throw BadRequestException("指定的球桌不存在或不属于该校区")
            }
        }
        
        // 自动分配：查找该校区空闲的球桌
        val availableTables = TableEntity.find { 
            (TableTable.campusId eq campusId) and (TableTable.status eq Status.free)
        }.toList()
        
        val date = LocalDate.parse(request.date)
        val startTime = LocalTime.parse(request.startTime)
        val endTime = LocalTime.parse(request.endTime)
        
        // 找到在指定时间段可用的球桌
        for (table in availableTables) {
            if (isTableAvailable(table, date, startTime, endTime)) {
                return table
            }
        }

        COURSE_SERVICE_LOGGER.warn("在校区 $campusId 未找到可用球桌，课程将在没有指定球桌的情况下创建")
        throw BadRequestException("没有可用的球桌")
    }
    
    /**
     * 检查球桌在指定时间段是否可用
     */
    private fun isTableAvailable(table: TableEntity, date: LocalDate, startTime: LocalTime, endTime: LocalTime): Boolean {
        val conflictingCourses = CourseEntity.find { 
            (CourseTable.tableId eq table.id.value) and
            (CourseTable.date eq date) and
            (CourseTable.status neq CourseStatus.CANCELLED)
        }
        
        return conflictingCourses.none { course ->
            (course.startTime <= startTime && course.endTime > startTime) ||
            (course.startTime < endTime && course.endTime >= endTime) ||
            (course.startTime >= startTime && course.endTime <= endTime)
        }
    }
}
