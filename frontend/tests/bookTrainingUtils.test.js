import { describe, it, expect } from 'vitest'
import { normalizeLevel, displayLevel, filterCoachesByLevel } from '@/views/student/bookTrainingUtils'

describe('bookTrainingUtils', () => {
  it('normalizeLevel should map various labels to canonical', () => {
    expect(normalizeLevel('初级')).toBe('初级')
    expect(normalizeLevel('初级教练')).toBe('初级')
    expect(normalizeLevel('中级教练')).toBe('中级')
    expect(normalizeLevel('高级教练')).toBe('高级')
    expect(normalizeLevel('')).toBe('')
    expect(normalizeLevel(null)).toBe('')
  })

  it('displayLevel should show with 教练 suffix or 未评级', () => {
    expect(displayLevel('初级')).toBe('初级教练')
    expect(displayLevel('中级教练')).toBe('中级教练')
    expect(displayLevel('未知')).toBe('未评级')
  })

  it('filterCoachesByLevel should filter correctly', () => {
    const coaches = [
      { id: '1', level: '初级教练' },
      { id: '2', level: '中级' },
      { id: '3', level: '高级教练' },
      { id: '4', level: '未评级' },
    ]
    expect(filterCoachesByLevel(coaches, '')).toHaveLength(4)
    expect(filterCoachesByLevel(coaches, '初级')).toEqual([{ id: '1', level: '初级教练' }])
    expect(filterCoachesByLevel(coaches, '中级')).toEqual([{ id: '2', level: '中级' }])
    expect(filterCoachesByLevel(coaches, '高级')).toEqual([{ id: '3', level: '高级教练' }])
  })
})
