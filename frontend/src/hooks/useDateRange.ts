// ========================================
// MOEWARE IMS - DATE RANGE HOOK
// ========================================

import { useState, useEffect } from "react"

export interface DateRange {
  startDate: Date | null
  endDate: Date | null
}

const STORAGE_KEY = "moeware_dashboard_date_range"

export function useDateRange() {
  const [dateRange, setDateRange] = useState<DateRange>(() => {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored) {
      try {
        const parsed = JSON.parse(stored)
        return {
          startDate: parsed.startDate ? new Date(parsed.startDate) : null,
          endDate: parsed.endDate ? new Date(parsed.endDate) : null,
        }
      } catch {
        return { startDate: null, endDate: null }
      }
    }
    return { startDate: null, endDate: null }
  })

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(dateRange))
  }, [dateRange])

  const updateDateRange = (range: DateRange) => {
    setDateRange(range)
  }

  const clearDateRange = () => {
    setDateRange({ startDate: null, endDate: null })
  }

  const hasDateRange = dateRange.startDate !== null || dateRange.endDate !== null

  // Format for display
  const formatDateRange = (): string => {
    if (!dateRange.startDate && !dateRange.endDate) return ""
    
    const options: Intl.DateTimeFormatOptions = { month: "short", day: "numeric", year: "numeric" }
    const start = dateRange.startDate ? dateRange.startDate.toLocaleDateString("en-US", options) : "..."
    const end = dateRange.endDate ? dateRange.endDate.toLocaleDateString("en-US", options) : "..."
    
    return `${start} - ${end}`
  }

  return {
    dateRange,
    updateDateRange,
    clearDateRange,
    hasDateRange,
    formatDateRange,
  }
}
