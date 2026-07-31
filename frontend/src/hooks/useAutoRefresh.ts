// ========================================
// MOEWARE IMS - AUTO REFRESH HOOK
// ========================================

import { useState, useEffect, useCallback, useRef } from "react"

export interface AutoRefreshOptions {
  enabled?: boolean
  interval?: number // in milliseconds
  onRefresh?: () => void
}

export interface AutoRefreshState {
  isRefreshing: boolean
  lastRefreshed: Date | null
  refreshCount: number
}

export function useAutoRefresh({
  enabled = false,
  interval = 30000, // default 30 seconds
  onRefresh,
}: AutoRefreshOptions) {
  const [isRefreshing, setIsRefreshing] = useState(false)
  const [lastRefreshed, setLastRefreshed] = useState<Date | null>(null)
  const [refreshCount, setRefreshCount] = useState(0)
  const [isEnabled, setIsEnabled] = useState(enabled)
  const intervalRef = useRef<number | null>(null)
  const onRefreshRef = useRef(onRefresh)

  // Update ref when callback changes
  useEffect(() => {
    onRefreshRef.current = onRefresh
  }, [onRefresh])

  const triggerRefresh = useCallback(async () => {
    if (isRefreshing) return
    
    setIsRefreshing(true)
    try {
      if (onRefreshRef.current) {
        await onRefreshRef.current()
      }
      setLastRefreshed(new Date())
      setRefreshCount((prev) => prev + 1)
    } finally {
      setIsRefreshing(false)
    }
  }, [isRefreshing])

  const toggleEnabled = useCallback(() => {
    setIsEnabled((prev) => !prev)
  }, [])

  const setEnabled = useCallback((enabled: boolean) => {
    setIsEnabled(enabled)
  }, [])

  // Handle interval
  useEffect(() => {
    if (isEnabled && interval > 0) {
      intervalRef.current = window.setInterval(() => {
        triggerRefresh()
      }, interval)
    }

    return () => {
      if (intervalRef.current) {
        clearInterval(intervalRef.current)
        intervalRef.current = null
      }
    }
  }, [isEnabled, interval, triggerRefresh])

  // Format last refreshed time
  const formatLastRefreshed = useCallback((): string => {
    if (!lastRefreshed) return "Never"
    
    const now = new Date()
    const diff = Math.floor((now.getTime() - lastRefreshed.getTime()) / 1000)
    
    if (diff < 5) return "Just now"
    if (diff < 60) return `${diff}s ago`
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`
    return lastRefreshed.toLocaleTimeString()
  }, [lastRefreshed])

  return {
    isRefreshing,
    lastRefreshed,
    refreshCount,
    isEnabled,
    triggerRefresh,
    toggleEnabled,
    setEnabled,
    formatLastRefreshed,
  }
}
