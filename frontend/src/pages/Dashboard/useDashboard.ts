import { useState } from "react"
import { useAuthStore } from "@/store/authStore"
import { useKPIVisibility } from "@/hooks/useKPIVisibility"
import { useDateRange } from "@/hooks/useDateRange"
import { useAutoRefresh } from "@/hooks/useAutoRefresh"
import { useTheme } from "@/hooks/useTheme"
// import {
//   useDashboardOverview,
//   useDashboardSalesAnalytics,
//   useDashboardTopSellingProducts,
//   useDashboardInventorySummary,
// } from "@/services/dashboard"
import type { TimeFilterValue } from "@/components/ui/TimeFilterBar"

interface KPIData {
  title: string
  value: string
  trend: number
  trendLabel: string
  sparklineData: number[]
}

interface UseDashboardReturn {
  // User
  user: ReturnType<typeof useAuthStore> extends () => infer R ? R : never
  
  // State
  selectedRange: TimeFilterValue
  setSelectedRange: (value: TimeFilterValue) => void
  salesChartCollapsed: boolean
  setSalesChartCollapsed: (value: boolean) => void
  isMetricsModalOpen: boolean
  setIsMetricsModalOpen: (value: boolean) => void
  isDatePickerOpen: boolean
  setIsDatePickerOpen: (value: boolean) => void
  
  // KPI visibility
  kpis: ReturnType<typeof useKPIVisibility>["kpis"]
  visibleKPIs: ReturnType<typeof useKPIVisibility>["visibleKPIs"]
  toggleKPI: ReturnType<typeof useKPIVisibility>["toggleKPI"]
  resetToDefault: ReturnType<typeof useKPIVisibility>["resetToDefault"]
  
  // Date range
  dateRange: ReturnType<typeof useDateRange>["dateRange"]
  updateDateRange: ReturnType<typeof useDateRange>["updateDateRange"]
  clearDateRange: ReturnType<typeof useDateRange>["clearDateRange"]
  hasDateRange: boolean
  formatDateRange: () => string
  
  // Auto-refresh
  isRefreshing: boolean
  autoRefreshEnabled: boolean
  toggleEnabled: () => void
  triggerRefresh: () => void
  formatLastRefreshed: () => string
  
  // Data (commented out until API is ready)
  // overviewData: ReturnType<typeof useDashboardOverview>["data"]
  // overviewLoading: boolean
  // refetchOverview: ReturnType<typeof useDashboardOverview>["refetch"]
  
  // salesAnalyticsData: ReturnType<typeof useDashboardSalesAnalytics>["data"]
  // salesLoading: boolean
  // refetchSalesAnalytics: ReturnType<typeof useDashboardSalesAnalytics>["refetch"]
  
  // topProductsData: ReturnType<typeof useDashboardTopSellingProducts>["data"]
  // topProductsLoading: boolean
  // refetchTopProducts: ReturnType<typeof useDashboardTopSellingProducts>["refetch"]
  
  // inventorySummaryData: ReturnType<typeof useDashboardInventorySummary>["data"]
  // inventorySummaryLoading: boolean
  // refetchInventorySummary: ReturnType<typeof useDashboardInventorySummary>["refetch"]
  
  // Data (placeholder until API is ready)
  overviewData: undefined
  overviewLoading: boolean
  refetchOverview: () => void
  
  salesAnalyticsData: undefined
  salesLoading: boolean
  refetchSalesAnalytics: () => void
  
  topProductsData: undefined
  topProductsLoading: boolean
  refetchTopProducts: () => void
  
  inventorySummaryData: undefined
  inventorySummaryLoading: boolean
  refetchInventorySummary: () => void
  
  // Derived data
  displayKPIs: { id: string } & KPIData[]
  isLoading: boolean
}

export function useDashboard(): UseDashboardReturn {
  const user = useAuthStore((state) => state.user)
  
  // State
  const [selectedRange, setSelectedRange] = useState<TimeFilterValue>("1m")
  const [salesChartCollapsed, setSalesChartCollapsed] = useState(false)
  const [isMetricsModalOpen, setIsMetricsModalOpen] = useState(false)
  const [isDatePickerOpen, setIsDatePickerOpen] = useState(false)
  
  // Subscribe to theme changes
  useTheme()
  
  // KPI visibility management
  const { kpis, visibleKPIs, toggleKPI, resetToDefault } = useKPIVisibility()
  
  // Date range management
  const { dateRange, updateDateRange, clearDateRange, hasDateRange, formatDateRange } = useDateRange()
  
  // Dashboard data queries (commented out until API is ready)
  // const { data: overviewData, isLoading: overviewLoading, refetch: refetchOverview } = useDashboardOverview()
  // const { data: salesAnalyticsData, isLoading: salesLoading, refetch: refetchSalesAnalytics } = useDashboardSalesAnalytics({ period: "MONTH" })
  // const { data: topProductsData, isLoading: topProductsLoading, refetch: refetchTopProducts } = useDashboardTopSellingProducts({ period: "MONTH", limit: 5 })
  // const { data: inventorySummaryData, isLoading: inventorySummaryLoading, refetch: refetchInventorySummary } = useDashboardInventorySummary()
  
  // Placeholder values until API is ready
  const overviewData = undefined
  const overviewLoading = false
  const refetchOverview = () => {}
  
  const salesAnalyticsData = undefined
  const salesLoading = false
  const refetchSalesAnalytics = () => {}
  
  const topProductsData = undefined
  const topProductsLoading = false
  const refetchTopProducts = () => {}
  
  const inventorySummaryData = undefined
  const inventorySummaryLoading = false
  const refetchInventorySummary = () => {}
  
  // Auto-refresh hook
  const { 
    isRefreshing, 
    isEnabled: autoRefreshEnabled, 
    toggleEnabled, 
    triggerRefresh,
    formatLastRefreshed 
  } = useAutoRefresh({
    enabled: false,
    interval: 30000,
    onRefresh: async () => {
      await Promise.all([
        refetchOverview(),
        refetchSalesAnalytics(),
        refetchTopProducts(),
        refetchInventorySummary(),
      ])
    }
  })
  
  // Transform overview data to KPI cards
  const kpiDataMap: Record<string, KPIData> = {
    revenue: {
      title: "Total Revenue",
      value: `$${(overviewData?.data?.revenue?.thisMonth || 0).toLocaleString()}`,
      trend: overviewData?.data?.revenue?.growth || 0,
      trendLabel: "vs last month",
      sparklineData: [
        overviewData?.data?.revenue?.lastMonth || 0,
        overviewData?.data?.revenue?.thisWeek || 0,
        overviewData?.data?.revenue?.thisMonth || 0,
      ],
    },
    products: {
      title: "Total Products",
      value: overviewData?.data?.metrics?.totalProducts?.toString() || "0",
      trend: 0,
      trendLabel: "in inventory",
      sparklineData: [],
    },
    lowStock: {
      title: "Low Stock Items",
      value: overviewData?.data?.metrics?.lowStockProducts?.toString() || "0",
      trend: 0,
      trendLabel: "need attention",
      sparklineData: [],
    },
    pendingOrders: {
      title: "Pending Orders",
      value: (overviewData?.data?.orders?.pendingSalesOrders || 0).toString(),
      trend: 0,
      trendLabel: "sales orders",
      sparklineData: [],
    },
    warehouses: {
      title: "Active Warehouses",
      value: overviewData?.data?.metrics?.totalWarehouses?.toString() || "0",
      trend: 0,
      trendLabel: "locations",
      sparklineData: [],
    },
    inventoryValue: {
      title: "Inventory Value",
      value: `$${(overviewData?.data?.metrics?.totalInventoryValue || 0).toLocaleString()}`,
      trend: 0,
      trendLabel: "total value",
      sparklineData: [],
    },
  }
  
  // Fallback values when no data
  const fallbackKpiDataMap: Record<string, KPIData> = {
    revenue: { title: "Total Revenue", value: "-", trend: 0, trendLabel: "vs last month", sparklineData: [] },
    products: { title: "Total Products", value: "-", trend: 0, trendLabel: "in inventory", sparklineData: [] },
    lowStock: { title: "Low Stock Items", value: "-", trend: 0, trendLabel: "need attention", sparklineData: [] },
    pendingOrders: { title: "Pending Orders", value: "-", trend: 0, trendLabel: "sales orders", sparklineData: [] },
    warehouses: { title: "Active Warehouses", value: "-", trend: 0, trendLabel: "locations", sparklineData: [] },
    inventoryValue: { title: "Inventory Value", value: "-", trend: 0, trendLabel: "total value", sparklineData: [] },
  }
  
  // Get KPIs to display based on visibility settings
  const displayKPIs = visibleKPIs.map((visibleKpi) => {
    const data = overviewData?.data ? kpiDataMap[visibleKpi.id] : fallbackKpiDataMap[visibleKpi.id]
    return { id: visibleKpi.id, ...data }
  })
  
  const isLoading = overviewLoading || salesLoading
  
  return {
    // User
    user,
    
    // State
    selectedRange,
    setSelectedRange,
    salesChartCollapsed,
    setSalesChartCollapsed,
    isMetricsModalOpen,
    setIsMetricsModalOpen,
    isDatePickerOpen,
    setIsDatePickerOpen,
    
    // KPI visibility
    kpis,
    visibleKPIs,
    toggleKPI,
    resetToDefault,
    
    // Date range
    dateRange,
    updateDateRange,
    clearDateRange,
    hasDateRange,
    formatDateRange,
    
    // Auto-refresh
    isRefreshing,
    autoRefreshEnabled,
    toggleEnabled,
    triggerRefresh,
    formatLastRefreshed,
    
    // Data
    overviewData,
    overviewLoading,
    refetchOverview,
    salesAnalyticsData,
    salesLoading,
    refetchSalesAnalytics,
    topProductsData,
    topProductsLoading,
    refetchTopProducts,
    inventorySummaryData,
    inventorySummaryLoading,
    refetchInventorySummary,
    
    // Derived data
    displayKPIs,
    isLoading,
  }
}
