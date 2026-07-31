// Dashboard Query Keys

export const dashboardKeys = {
  all: ['dashboard'] as const,
  overview: () => [...dashboardKeys.all, 'overview'] as const,
  inventorySummary: () => [...dashboardKeys.all, 'inventorySummary'] as const,
  salesAnalytics: () => [...dashboardKeys.all, 'salesAnalytics'] as const,
  purchaseAnalytics: () => [...dashboardKeys.all, 'purchaseAnalytics'] as const,
  lowStockAlerts: () => [...dashboardKeys.all, 'lowStockAlerts'] as const,
  pendingActions: () => [...dashboardKeys.all, 'pendingActions'] as const,
  activityFeed: () => [...dashboardKeys.all, 'activityFeed'] as const,
  inventoryTrend: () => [...dashboardKeys.all, 'inventoryTrend'] as const,
  salesTrend: () => [...dashboardKeys.all, 'salesTrend'] as const,
  topSellingProducts: () => [...dashboardKeys.all, 'topSellingProducts'] as const,
}
