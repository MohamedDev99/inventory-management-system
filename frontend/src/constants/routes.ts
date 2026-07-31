// ========================================
// MOEWARE IMS - ROUTE PATHS
// ========================================

// ========================================
// AUTH ROUTES
// ========================================

export const AUTH_ROUTES = {
  LANDING: "/",
  LOGIN: "/login",
  REGISTER: "/register",
  FORGOT_PASSWORD: "/forgot-password",
  RESET_PASSWORD: "/reset-password",
  ONBOARDING: "/onboarding",
} as const

// ========================================
// MAIN APP ROUTES
// ========================================

export const APP_ROUTES = {
  LANDING: "/",
  DASHBOARD: "/dashboard",
  NOTIFICATIONS: "/notifications",
  SETTINGS: "/settings",
  REPORTS: "/reports",
} as const

// ========================================
// ENTITY ROUTES
// ========================================

export const ENTITY_ROUTES = {
  PRODUCTS: "/products",
  CATEGORIES: "/categories",
  SUPPLIERS: "/suppliers",
  WAREHOUSES: "/warehouses",
  STOCK: "/stock",
  CUSTOMERS: "/customers",
  EMPLOYEES: "/employees",
  DEPARTMENTS: "/departments",
  PURCHASE_ORDERS: "/purchase-orders",
  SALES_ORDERS: "/sales-orders",
  SHIPMENTS: "/shipments",
  PAYMENTS: "/payments",
  INVOICES: "/invoices",
} as const

// ========================================
// API ENDPOINTS
// ========================================

export const API_ENDPOINTS = {
  // Auth
  AUTH: {
    LOGIN: "/auth/login",
    REGISTER: "/auth/register",
    REFRESH: "/auth/refresh",
    LOGOUT: "/auth/logout",
    FORGOT_PASSWORD: "/auth/forgot-password",
    RESET_PASSWORD: "/auth/reset-password",
    ONBOARDING: "/auth/onboarding",
    CURRENT_USER: "/auth/me",
  },

  // Products
  PRODUCTS: "/products",
  PRODUCT_BY_ID: (id: number | string) => `/products/${id}`,
  PRODUCT_BY_BARCODE: (barcode: string) => `/products/barcode/${barcode}`,
  PRODUCT_BY_SKU: (sku: string) => `/products/sku/${sku}`,
  LOW_STOCK_PRODUCTS: "/products/low-stock",

  // Categories
  CATEGORIES: "/categories",
  CATEGORY_BY_ID: (id: number | string) => `/categories/${id}`,

  // Suppliers
  SUPPLIERS: "/suppliers",
  SUPPLIER_BY_ID: (id: number | string) => `/suppliers/${id}`,
  SUPPLIER_PERFORMANCE: (id: number | string) => `/suppliers/${id}/performance`,
  SUPPLIER_ORDERS: (id: number | string) => `/suppliers/${id}/orders`,

  // Warehouses
  WAREHOUSES: "/warehouses",
  WAREHOUSE_BY_ID: (id: number | string) => `/warehouses/${id}`,
  WAREHOUSE_STATS: (id: number | string) => `/warehouses/${id}/stats`,

  // Stock
  STOCK: "/stock",
  STOCK_BY_ID: (id: number | string) => `/stock/${id}`,
  STOCK_TRANSFER: "/stock/transfer",
  STOCK_ADJUSTMENT: "/stock/adjustment",

  // Customers
  CUSTOMERS: "/customers",
  CUSTOMER_BY_ID: (id: number | string) => `/customers/${id}`,

  // Employees
  EMPLOYEES: "/employees",
  EMPLOYEE_BY_ID: (id: number | string) => `/employees/${id}`,

  // Departments
  DEPARTMENTS: "/departments",
  DEPARTMENT_BY_ID: (id: number | string) => `/departments/${id}`,

  // Purchase Orders
  PURCHASE_ORDERS: "/purchase-orders",
  PURCHASE_ORDER_BY_ID: (id: number | string) => `/purchase-orders/${id}`,
  PURCHASE_ORDER_APPROVE: (id: number | string) => `/purchase-orders/${id}/approve`,
  PURCHASE_ORDER_REJECT: (id: number | string) => `/purchase-orders/${id}/reject`,
  PURCHASE_ORDER_RECEIVE: (id: number | string) => `/purchase-orders/${id}/receive`,

  // Sales Orders
  SALES_ORDERS: "/sales-orders",
  SALES_ORDER_BY_ID: (id: number | string) => `/sales-orders/${id}`,
  SALES_ORDER_CONFIRM: (id: number | string) => `/sales-orders/${id}/confirm`,
  SALES_ORDER_CANCEL: (id: number | string) => `/sales-orders/${id}/cancel`,

  // Shipments
  SHIPMENTS: "/shipments",
  SHIPMENT_BY_ID: (id: number | string) => `/shipments/${id}`,
  SHIPMENT_BY_TRACKING: (trackingNumber: string) => `/shipments/tracking/${trackingNumber}`,
  SHIPMENT_DELIVER: (id: number | string) => `/shipments/${id}/deliver`,
  SHIPMENT_UPDATE_STATUS: (id: number | string) => `/shipments/${id}/status`,

  // Payments
  PAYMENTS: "/payments",
  PAYMENT_BY_ID: (id: number | string) => `/payments/${id}`,

  // Invoices
  INVOICES: "/invoices",
  INVOICE_BY_ID: (id: number | string) => `/invoices/${id}`,
  INVOICE_DOWNLOAD: (id: number | string) => `/invoices/${id}/download`,

  // Dashboard
  DASHBOARD: {
    OVERVIEW: "/dashboard/overview",
    SALES_ANALYTICS: "/dashboard/sales-analytics",
    PURCHASE_ANALYTICS: "/dashboard/purchase-analytics",
    INVENTORY_SUMMARY: "/dashboard/inventory-summary",
    INVENTORY_TREND: "/dashboard/inventory-trend",
    SALES_TREND: "/dashboard/sales-trend",
    TOP_SELLING_PRODUCTS: "/dashboard/top-selling-products",
    LOW_STOCK_ALERTS: "/dashboard/low-stock-alerts",
    PENDING_ACTIONS: "/dashboard/pending-actions",
    ACTIVITY_FEED: "/dashboard/activity-feed",
  },

  // Reports
  REPORTS: {
    BASE: "/reports",
    TYPES: "/reports/types",
    GENERATE: "/reports/generate",
    BY_ID: (id: number | string) => `/reports/${id}`,
    DOWNLOAD: (id: number | string) => `/reports/${id}/download`,
    SCHEDULED: "/reports/scheduled",
    SCHEDULE: "/reports/schedule",
    SCHEDULE_BY_ID: (id: number | string) => `/reports/scheduled/${id}`,
  },

  // Notifications
  NOTIFICATIONS: {
    BASE: "/notifications",
    UNREAD_COUNT: "/notifications/unread",
    MARK_READ: (id: number | string) => `/notifications/${id}/read`,
    MARK_ALL_READ: "/notifications/read-all",
    CLEAR_READ: "/notifications/clear-read",
    PREFERENCES: "/notifications/preferences",
  },

  // Users
  USERS: "/users",
  USER_BY_ID: (id: number | string) => `/users/${id}`,
  USER_INVITE: "/users/invite",

  // Roles
  ROLES: "/roles",
  ROLE_BY_ID: (id: number | string) => `/roles/${id}`,

  // Audit Logs
  AUDIT_LOGS: "/audit-logs",
} as const

// ========================================
// PAGINATION DEFAULTS
// ========================================

export const PAGINATION_DEFAULTS = {
  DEFAULT_PAGE: 0,
  DEFAULT_SIZE: 10,
  MAX_SIZE: 100,
} as const

// ========================================
// TIME RANGES
// ========================================

export const TIME_RANGES = [
  { value: "1d", label: "1D" },
  { value: "7d", label: "7D" },
  { value: "1m", label: "1M" },
  { value: "3m", label: "3M" },
  { value: "6m", label: "6M" },
  { value: "1y", label: "1Y" },
  { value: "3y", label: "3Y" },
  { value: "5y", label: "5Y" },
] as const
