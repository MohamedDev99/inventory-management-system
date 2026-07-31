import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema } from "@/lib/schemas/common/api"

// Dashboard overview schema
const dashboardOverviewSchema = z.object({
  metrics: z.object({
    totalProducts: z.number(),
    totalInventoryValue: z.number(),
    lowStockProducts: z.number(),
    outOfStockProducts: z.number(),
    totalWarehouses: z.number(),
    activeUsers: z.number(),
  }),
  orders: z.object({
    pendingSalesOrders: z.number(),
    confirmedSalesOrders: z.number(),
    pendingPurchaseOrders: z.number(),
    approvedPurchaseOrders: z.number(),
  }),
  recentActivity: z.object({
    salesOrdersToday: z.number(),
    purchaseOrdersToday: z.number(),
    shipmentsToday: z.number(),
    receivedToday: z.number(),
  }),
  alerts: z.object({
    lowStockAlerts: z.number(),
    pendingApprovals: z.number(),
    overdueInvoices: z.number(),
    pendingAdjustments: z.number(),
  }),
  revenue: z.object({
    today: z.number(),
    thisWeek: z.number(),
    thisMonth: z.number(),
    lastMonth: z.number(),
    growth: z.number(),
  }),
})

export type DashboardOverview = z.infer<typeof dashboardOverviewSchema>

// GET /api/dashboard/overview
export async function getDashboardOverview() {
  const response = await api.get("/dashboard/overview")
  const validated = validateResponse(
    apiResponseSchema(dashboardOverviewSchema),
    response.data,
    { prefix: "Get Dashboard Overview Response" }
  )
  return validated
}

// GET /api/dashboard/inventory-summary
export async function getDashboardInventorySummary(params: { warehouseId?: number; categoryId?: number } = {}) {
  const response = await api.get("/dashboard/inventory-summary", { params })
  // Return raw response for complex chart data
  return response.data
}

// GET /api/dashboard/sales-analytics
export async function getDashboardSalesAnalytics(params: { period?: "TODAY" | "WEEK" | "MONTH" | "QUARTER" | "YEAR"; startDate?: string; endDate?: string } = {}) {
  const response = await api.get("/dashboard/sales-analytics", { params })
  // Return raw response for complex chart data
  return response.data
}

// GET /api/dashboard/purchase-analytics
export async function getDashboardPurchaseAnalytics(params: { period?: "TODAY" | "WEEK" | "MONTH" | "QUARTER" | "YEAR"; startDate?: string; endDate?: string } = {}) {
  const response = await api.get("/dashboard/purchase-analytics", { params })
  // Return raw response for complex chart data
  return response.data
}

// GET /api/dashboard/low-stock-alerts
export async function getDashboardLowStockAlerts() {
  const response = await api.get("/dashboard/low-stock-alerts")
  // Return raw response for alerts data
  return response.data
}

// GET /api/dashboard/pending-actions
export async function getDashboardPendingActions() {
  const response = await api.get("/dashboard/pending-actions")
  // Return raw response for pending actions
  return response.data
}

// GET /api/dashboard/activity-feed
export async function getDashboardActivityFeed(limit: number = 20) {
  const response = await api.get("/dashboard/activity-feed", { params: { limit } })
  // Return raw response for activity feed
  return response.data
}

// GET /api/dashboard/charts/inventory-trend
export async function getDashboardInventoryTrend(params: { period?: "WEEK" | "MONTH" | "QUARTER" | "YEAR"; warehouseId?: number } = {}) {
  const response = await api.get("/dashboard/charts/inventory-trend", { params })
  // Return raw response for chart data
  return response.data
}

// GET /api/dashboard/charts/sales-trend
export async function getDashboardSalesTrend(params: { period?: "WEEK" | "MONTH" | "QUARTER" | "YEAR"; warehouseId?: number } = {}) {
  const response = await api.get("/dashboard/charts/sales-trend", { params })
  // Return raw response for chart data
  return response.data
}

// GET /api/dashboard/charts/top-selling-products
export async function getDashboardTopSellingProducts(params: { period?: "WEEK" | "MONTH" | "QUARTER" | "YEAR"; limit?: number } = {}) {
  const response = await api.get("/dashboard/charts/top-selling-products", { params })
  // Return raw response for chart data
  return response.data
}
