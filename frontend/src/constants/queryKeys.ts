// Query Keys for React Query
// Centralized query key management for all entities

export const queryKeys = {
  // Auth
  auth: {
    all: ["auth"] as const,
    currentUser: () => [...queryKeys.auth.all, "currentUser"] as const,
  },

  // Dashboard
  dashboard: {
    all: ["dashboard"] as const,
    stats: () => [...queryKeys.dashboard.all, "stats"] as const,
    charts: () => [...queryKeys.dashboard.all, "charts"] as const,
  },

  // Products
  products: {
    all: ["products"] as const,
    lists: () => [...queryKeys.products.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.products.lists(), params] as const,
    details: () => [...queryKeys.products.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.products.details(), id] as const,
    categories: () => [...queryKeys.products.all, "categories"] as const,
  },

  // Suppliers
  suppliers: {
    all: ["suppliers"] as const,
    lists: () => [...queryKeys.suppliers.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.suppliers.lists(), params] as const,
    details: () => [...queryKeys.suppliers.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.suppliers.details(), id] as const,
  },

  // Categories
  categories: {
    all: ["categories"] as const,
    lists: () => [...queryKeys.categories.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.categories.lists(), params] as const,
    details: () => [...queryKeys.categories.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.categories.details(), id] as const,
  },

  // Warehouses
  warehouses: {
    all: ["warehouses"] as const,
    lists: () => [...queryKeys.warehouses.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.warehouses.lists(), params] as const,
    details: () => [...queryKeys.warehouses.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.warehouses.details(), id] as const,
  },

  // Stock
  stock: {
    all: ["stock"] as const,
    lists: () => [...queryKeys.stock.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.stock.lists(), params] as const,
    details: () => [...queryKeys.stock.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.stock.details(), id] as const,
  },

  // Purchase Orders
  purchaseOrders: {
    all: ["purchaseOrders"] as const,
    lists: () => [...queryKeys.purchaseOrders.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.purchaseOrders.lists(), params] as const,
    details: () => [...queryKeys.purchaseOrders.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.purchaseOrders.details(), id] as const,
  },

  // Sales Orders
  salesOrders: {
    all: ["salesOrders"] as const,
    lists: () => [...queryKeys.salesOrders.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.salesOrders.lists(), params] as const,
    details: () => [...queryKeys.salesOrders.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.salesOrders.details(), id] as const,
  },

  // Shipments
  shipments: {
    all: ["shipments"] as const,
    lists: () => [...queryKeys.shipments.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.shipments.lists(), params] as const,
    details: () => [...queryKeys.shipments.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.shipments.details(), id] as const,
  },

  // Customers
  customers: {
    all: ["customers"] as const,
    lists: () => [...queryKeys.customers.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.customers.lists(), params] as const,
    details: () => [...queryKeys.customers.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.customers.details(), id] as const,
  },

  // Employees
  employees: {
    all: ["employees"] as const,
    lists: () => [...queryKeys.employees.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.employees.lists(), params] as const,
    details: () => [...queryKeys.employees.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.employees.details(), id] as const,
  },

  // Departments
  departments: {
    all: ["departments"] as const,
    lists: () => [...queryKeys.departments.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.departments.lists(), params] as const,
    details: () => [...queryKeys.departments.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.departments.details(), id] as const,
  },

  // Payments
  payments: {
    all: ["payments"] as const,
    lists: () => [...queryKeys.payments.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.payments.lists(), params] as const,
    details: () => [...queryKeys.payments.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.payments.details(), id] as const,
  },

  // Invoices
  invoices: {
    all: ["invoices"] as const,
    lists: () => [...queryKeys.invoices.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.invoices.lists(), params] as const,
    details: () => [...queryKeys.invoices.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.invoices.details(), id] as const,
  },

  // Reports
  reports: {
    all: ["reports"] as const,
    lists: () => [...queryKeys.reports.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.reports.lists(), params] as const,
    details: () => [...queryKeys.reports.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.reports.details(), id] as const,
    scheduled: () => [...queryKeys.reports.all, "scheduled"] as const,
  },

  // Notifications
  notifications: {
    all: ["notifications"] as const,
    lists: () => [...queryKeys.notifications.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.notifications.lists(), params] as const,
    details: () => [...queryKeys.notifications.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.notifications.details(), id] as const,
    unread: () => [...queryKeys.notifications.all, "unread"] as const,
    preferences: () => [...queryKeys.notifications.all, "preferences"] as const,
  },

  // Users (for user management)
  users: {
    all: ["users"] as const,
    lists: () => [...queryKeys.users.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.users.lists(), params] as const,
    details: () => [...queryKeys.users.all, "detail"] as const,
    detail: (id: number) => [...queryKeys.users.details(), id] as const,
  },

  // Audit Logs
  auditLogs: {
    all: ["auditLogs"] as const,
    lists: () => [...queryKeys.auditLogs.all, "list"] as const,
    list: (params?: Record<string, unknown>) => [...queryKeys.auditLogs.lists(), params] as const,
  },
}

// Stale times configuration (in milliseconds)
export const staleTimes = {
  // Frequently refreshed data
  dashboard: 30 * 1000, // 30 seconds
  notifications: 60 * 1000, // 1 minute
  
  // Semi-static data
  categories: 300 * 1000, // 5 minutes
  warehouses: 300 * 1000, // 5 minutes
  
  // Default for other entities
  default: 30 * 1000, // 30 seconds
}
