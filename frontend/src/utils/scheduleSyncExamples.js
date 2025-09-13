/**
 * 课表同步功能工具类
 * 提供与外部日历系统的同步、导出和邮件功能
 */

import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

export function useScheduleSync() {
  /**
   * 导出课表为 iCal 格式文件
   * @param {Array} schedules - 课表数据
   * @param {string} filename - 文件名
   */
  const exportSchedule = (schedules, filename = 'schedule.ics') => {
    if (!Array.isArray(schedules)) {
      ElMessage.error('课表数据无效，无法导出。');
      return false;
    }
    try {
      // 生成 iCal 格式内容
      let icalContent = 'BEGIN:VCALENDAR\n'
      icalContent += 'VERSION:2.0\n'
      icalContent += 'PRODID:-//乒乓球训练系统//课表导出//CN\n'
      icalContent += 'CALSCALE:GREGORIAN\n'
      icalContent += 'METHOD:PUBLISH\n'

      schedules.forEach(schedule => {
        const startDateTime = dayjs(`${schedule.date} ${schedule.startTime}`).format('YYYYMMDDTHHmmss')
        const endDateTime = dayjs(`${schedule.date} ${schedule.endTime}`).format('YYYYMMDDTHHmmss')
        const now = dayjs().format('YYYYMMDDTHHmmss')

        icalContent += 'BEGIN:VEVENT\n'
        icalContent += `UID:${schedule.id}@tabletennis-training.com\n`
        icalContent += `DTSTAMP:${now}\n`
        icalContent += `DTSTART:${startDateTime}\n`
        icalContent += `DTEND:${endDateTime}\n`
        icalContent += `SUMMARY:${schedule.title}\n`
        icalContent += `DESCRIPTION:${schedule.description || '乒乓球训练课程'}\n`
        icalContent += `LOCATION:${schedule.location}\n`
        icalContent += `STATUS:${schedule.status === 'confirmed' ? 'CONFIRMED' : 'TENTATIVE'}\n`
        icalContent += 'END:VEVENT\n'
      })

      icalContent += 'END:VCALENDAR'

      // 创建并下载文件
      const blob = new Blob([icalContent], { type: 'text/calendar;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)

      return true
    } catch (error) {
      console.error('导出课表失败:', error)
      throw error
    }
  }

  /**
   * 同步课表到外部日历平台
   * @param {string} platform - 平台类型 ('google', 'outlook', 'apple')
   */
  const syncToCalendar = async (platform) => {
    try {
      // 模拟同步过程
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      switch (platform) {
        case 'google':
          // 实际应用中这里会调用 Google Calendar API
          console.log('同步到 Google Calendar')
          break
        case 'outlook':
          // 实际应用中这里会调用 Outlook Calendar API
          console.log('同步到 Outlook Calendar')
          break
        case 'apple':
          // 实际应用中这里会调用 Apple Calendar API
          console.log('同步到 Apple Calendar')
          break
        default:
          throw new Error('不支持的平台')
      }

      return true
    } catch (error) {
      console.error('日历同步失败:', error)
      throw error
    }
  }

  /**
   * 同步课表到移动设备
   * @param {string} deviceId - 设备ID
   * @param {Array} schedules - 课表数据
   */
  const syncToMobile = async (deviceId, schedules) => {
    try {
      // 模拟移动端同步
      await new Promise(resolve => setTimeout(resolve, 1500))
      
      // 实际应用中这里会调用移动端同步API
      console.log('同步到移动设备:', { deviceId, scheduleCount: schedules.length })
      
      return true
    } catch (error) {
      console.error('移动端同步失败:', error)
      throw error
    }
  }

  /**
   * 发送课表邮件
   * @param {string} type - 邮件类型 ('schedule', 'reminder', 'cancellation')
   * @param {Object} options - 邮件选项
   */
  const sendEmail = async (type, options) => {
    try {
      const { email, name, scheduleData } = options
      
      // 模拟邮件发送
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // 实际应用中这里会调用邮件服务API
      console.log('发送邮件:', {
        type,
        to: email,
        name,
        scheduleCount: scheduleData?.length || 0
      })
      
      return true
    } catch (error) {
      console.error('邮件发送失败:', error)
      throw error
    }
  }

  /**
   * 生成课表摘要
   * @param {Array} schedules - 课表数据
   * @returns {Object} 摘要信息
   */
  const generateScheduleSummary = (schedules) => {
    const summary = {
      total: schedules.length,
      byType: {},
      byStatus: {},
      upcomingWeek: 0,
      thisMonth: 0
    }

    const now = dayjs()
    const weekEnd = now.add(7, 'day')
    const monthEnd = now.endOf('month')

    schedules.forEach(schedule => {
      // 按类型统计
      summary.byType[schedule.type] = (summary.byType[schedule.type] || 0) + 1
      
      // 按状态统计
      summary.byStatus[schedule.status] = (summary.byStatus[schedule.status] || 0) + 1
      
      const scheduleDate = dayjs(schedule.date)
      
      // 未来一周的课程
      if (scheduleDate.isAfter(now) && scheduleDate.isBefore(weekEnd)) {
        summary.upcomingWeek++
      }
      
      // 本月课程
      if (scheduleDate.isSame(now, 'month')) {
        summary.thisMonth++
      }
    })

    return summary
  }

  /**
   * 检查课程冲突
   * @param {Array} schedules - 现有课表
   * @param {Object} newSchedule - 新课程
   * @returns {Array} 冲突的课程列表
   */
  const checkScheduleConflicts = (schedules, newSchedule) => {
    // 参数校验
    if (
      !Array.isArray(schedules) ||
      typeof newSchedule !== 'object' ||
      newSchedule === null ||
      typeof newSchedule.date !== 'string' ||
      typeof newSchedule.startTime !== 'string' ||
      typeof newSchedule.endTime !== 'string'
    ) {
      // 可以选择抛出错误或返回空数组，这里返回空数组
      return []
    }
    const conflicts = []
    const newStart = dayjs(`${newSchedule.date} ${newSchedule.startTime}`)
    const newEnd = dayjs(`${newSchedule.date} ${newSchedule.endTime}`)

    schedules.forEach(schedule => {
      if (schedule.id === newSchedule.id) return // 跳过自己
      
      const existingStart = dayjs(`${schedule.date} ${schedule.startTime}`)
      const existingEnd = dayjs(`${schedule.date} ${schedule.endTime}`)
      
      // 检查时间重叠
      if (
        (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart))
      ) {
        conflicts.push(schedule)
      }
    })

    return conflicts
  }

  /**
   * 格式化课程时间为人类可读格式
   * @param {Object} schedule - 课程信息
   * @returns {string} 格式化的时间字符串
   */
  const formatScheduleTime = (schedule) => {
    const date = dayjs(schedule.date)
    const startTime = schedule.startTime
    const endTime = schedule.endTime
    
    return `${date.format('YYYY年MM月DD日')} ${startTime}-${endTime}`
  }

  /**
   * 获取课程状态的中文描述
   * @param {string} status - 课程状态
   * @returns {string} 中文描述
   */
  const getStatusDescription = (status) => {
    const statusMap = {
      pending: '待确认',
      confirmed: '已确认',
      cancelled: '已取消',
      completed: '已完成',
      rescheduled: '已改期'
    }
    return statusMap[status] || status
  }

  return {
    exportSchedule,
    syncToCalendar,
    syncToMobile,
    sendEmail,
    generateScheduleSummary,
    checkScheduleConflicts,
    formatScheduleTime,
    getStatusDescription
  }
}

export default useScheduleSync