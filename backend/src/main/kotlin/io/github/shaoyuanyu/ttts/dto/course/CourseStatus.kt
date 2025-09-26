package io.github.shaoyuanyu.ttts.dto.course

enum class CourseStatus {
    PENDING,    // 学生预约，等待教练确认
    CONFIRMED,  // 教练确认，课程安排确定
    COMPLETED,  // 课程已完成
    CANCELLED   // 课程已取消
}
