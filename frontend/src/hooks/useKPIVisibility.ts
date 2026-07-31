// ========================================
// MOEWARE IMS - KPI VISIBILITY HOOK
// ========================================

import { useState, useEffect } from "react"

export interface KPIConfig {
  id: string
  title: string
  visible: boolean
}

const DEFAULT_KPIS: KPIConfig[] = [
  { id: "revenue", title: "Total Revenue", visible: true },
  { id: "products", title: "Total Products", visible: true },
  { id: "lowStock", title: "Low Stock Items", visible: true },
  { id: "pendingOrders", title: "Pending Orders", visible: true },
  { id: "warehouses", title: "Active Warehouses", visible: true },
  { id: "inventoryValue", title: "Inventory Value", visible: true },
]

const STORAGE_KEY = "moeware_dashboard_kpis"

export function useKPIVisibility() {
  const [kpis, setKpis] = useState<KPIConfig[]>(() => {
    const stored = localStorage.getItem(STORAGE_KEY)
    if (stored) {
      try {
        return JSON.parse(stored)
      } catch {
        return DEFAULT_KPIS
      }
    }
    return DEFAULT_KPIS
  })

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(kpis))
  }, [kpis])

  const toggleKPI = (id: string) => {
    setKpis((prev) =>
      prev.map((kpi) =>
        kpi.id === id ? { ...kpi, visible: !kpi.visible } : kpi
      )
    )
  }

  const setKPIVisibility = (id: string, visible: boolean) => {
    setKpis((prev) =>
      prev.map((kpi) =>
        kpi.id === id ? { ...kpi, visible } : kpi
      )
    )
  }

  const resetToDefault = () => {
    setKpis(DEFAULT_KPIS)
  }

  const visibleKPIs = kpis.filter((kpi) => kpi.visible)

  return {
    kpis,
    visibleKPIs,
    toggleKPI,
    setKPIVisibility,
    resetToDefault,
  }
}
