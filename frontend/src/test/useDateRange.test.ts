import { describe, it, expect, vi, beforeEach, afterEach } from "vitest"
import { useDateRange } from "@/hooks/useDateRange"
import { renderHook, act } from "@testing-library/react"

const STORAGE_KEY = "moeware_dashboard_date_range"

describe("useDateRange", () => {
  beforeEach(() => {
    localStorage.clear()
  })

  afterEach(() => {
    localStorage.clear()
  })

  it("should return default empty date range", () => {
    const { result } = renderHook(() => useDateRange())
    
    expect(result.current.dateRange).toEqual({
      startDate: null,
      endDate: null,
    })
    expect(result.current.hasDateRange).toBe(false)
  })

  it("should update date range", () => {
    const { result } = renderHook(() => useDateRange())
    
    const newRange = {
      startDate: new Date("2024-01-01"),
      endDate: new Date("2024-01-31"),
    }
    
    act(() => {
      result.current.updateDateRange(newRange)
    })
    
    expect(result.current.dateRange.startDate).toEqual(new Date("2024-01-01"))
    expect(result.current.dateRange.endDate).toEqual(new Date("2024-01-31"))
    expect(result.current.hasDateRange).toBe(true)
  })

  it("should clear date range", () => {
    const { result } = renderHook(() => useDateRange())
    
    act(() => {
      result.current.updateDateRange({
        startDate: new Date("2024-01-01"),
        endDate: new Date("2024-01-31"),
      })
    })
    
    act(() => {
      result.current.clearDateRange()
    })
    
    expect(result.current.dateRange).toEqual({
      startDate: null,
      endDate: null,
    })
    expect(result.current.hasDateRange).toBe(false)
  })

  it("should persist date range to localStorage", () => {
    const { result } = renderHook(() => useDateRange())
    
    act(() => {
      result.current.updateDateRange({
        startDate: new Date("2024-06-15"),
        endDate: new Date("2024-06-20"),
      })
    })
    
    const stored = localStorage.getItem(STORAGE_KEY)
    expect(stored).toBeTruthy()
    
    const parsed = JSON.parse(stored!)
    expect(new Date(parsed.startDate)).toEqual(new Date("2024-06-15"))
  })

  it("should load date range from localStorage", () => {
    const storedData = {
      startDate: "2024-03-01T00:00:00.000Z",
      endDate: "2024-03-31T00:00:00.000Z",
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(storedData))
    
    const { result } = renderHook(() => useDateRange())
    
    expect(result.current.dateRange.startDate).toEqual(new Date("2024-03-01"))
    expect(result.current.dateRange.endDate).toEqual(new Date("2024-03-31"))
  })

  it("should handle invalid localStorage data gracefully", () => {
    localStorage.setItem(STORAGE_KEY, "invalid json")
    
    const { result } = renderHook(() => useDateRange())
    
    expect(result.current.dateRange).toEqual({
      startDate: null,
      endDate: null,
    })
  })

  describe("formatDateRange", () => {
    it("should return empty string when no dates", () => {
      const { result } = renderHook(() => useDateRange())
      
      expect(result.current.formatDateRange()).toBe("")
    })

    it("should format date range with both dates", () => {
      const { result } = renderHook(() => useDateRange())
      
      act(() => {
        result.current.updateDateRange({
          startDate: new Date("2024-01-15"),
          endDate: new Date("2024-01-20"),
        })
      })
      
      const formatted = result.current.formatDateRange()
      expect(formatted).toMatch(/Jan/)
    })

    it("should show placeholder when only start date", () => {
      const { result } = renderHook(() => useDateRange())
      
      act(() => {
        result.current.updateDateRange({
          startDate: new Date("2024-01-15"),
          endDate: null,
        })
      })
      
      const formatted = result.current.formatDateRange()
      expect(formatted).toMatch(/\.\.\./)
    })

    it("should show placeholder when only end date", () => {
      const { result } = renderHook(() => useDateRange())
      
      act(() => {
        result.current.updateDateRange({
          startDate: null,
          endDate: new Date("2024-01-20"),
        })
      })
      
      const formatted = result.current.formatDateRange()
      expect(formatted).toMatch(/\.\.\./)
    })
  })
})