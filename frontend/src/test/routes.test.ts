import { describe, it, expect } from "vitest"
import { AUTH_ROUTES, APP_ROUTES, ENTITY_ROUTES, API_ENDPOINTS, PAGINATION_DEFAULTS, TIME_RANGES } from "@/constants/routes"
describe("routes", () => {
  describe("AUTH_ROUTES", () => {
    it("should have all required auth routes", () => {
      expect(AUTH_ROUTES.LANDING).toBe("/")
      expect(AUTH_ROUTES.LOGIN).toBe("/login")
      expect(AUTH_ROUTES.REGISTER).toBe("/register")
      expect(AUTH_ROUTES.FORGOT_PASSWORD).toBe("/forgot-password")
      expect(AUTH_ROUTES.RESET_PASSWORD).toBe("/reset-password")
      expect(AUTH_ROUTES.ONBOARDING).toBe("/onboarding")
    })

    it("should start with / for all routes", () => {
      Object.values(AUTH_ROUTES).forEach((route) => {
        expect(route).toMatch(/^\//)
      })
    })
  })

  describe("APP_ROUTES", () => {
    it("should have all required app routes", () => {
      expect(APP_ROUTES.LANDING).toBe("/")
      expect(APP_ROUTES.DASHBOARD).toBe("/dashboard")
      expect(APP_ROUTES.NOTIFICATIONS).toBe("/notifications")
      expect(APP_ROUTES.SETTINGS).toBe("/settings")
      expect(APP_ROUTES.REPORTS).toBe("/reports")
    })
  })

  describe("ENTITY_ROUTES", () => {
    it("should have all entity routes", () => {
      expect(ENTITY_ROUTES.PRODUCTS).toBe("/products")
      expect(ENTITY_ROUTES.CATEGORIES).toBe("/categories")
      expect(ENTITY_ROUTES.SUPPLIERS).toBe("/suppliers")
      expect(ENTITY_ROUTES.WAREHOUSES).toBe("/warehouses")
      expect(ENTITY_ROUTES.STOCK).toBe("/stock")
      expect(ENTITY_ROUTES.CUSTOMERS).toBe("/customers")
      expect(ENTITY_ROUTES.EMPLOYEES).toBe("/employees")
      expect(ENTITY_ROUTES.DEPARTMENTS).toBe("/departments")
      expect(ENTITY_ROUTES.PURCHASE_ORDERS).toBe("/purchase-orders")
      expect(ENTITY_ROUTES.SALES_ORDERS).toBe("/sales-orders")
      expect(ENTITY_ROUTES.SHIPMENTS).toBe("/shipments")
      expect(ENTITY_ROUTES.PAYMENTS).toBe("/payments")
      expect(ENTITY_ROUTES.INVOICES).toBe("/invoices")
    })
  })

  describe("API_ENDPOINTS", () => {
    describe("AUTH endpoints", () => {
      it("should have correct auth endpoints", () => {
        expect(API_ENDPOINTS.AUTH.LOGIN).toBe("/auth/login")
        expect(API_ENDPOINTS.AUTH.REGISTER).toBe("/auth/register")
        expect(API_ENDPOINTS.AUTH.REFRESH).toBe("/auth/refresh")
        expect(API_ENDPOINTS.AUTH.LOGOUT).toBe("/auth/logout")
        expect(API_ENDPOINTS.AUTH.CURRENT_USER).toBe("/auth/me")
      })
    })

    describe("PRODUCT_By_ID function", () => {
      it("should return correct path for numeric id", () => {
        expect(API_ENDPOINTS.PRODUCT_BY_ID(123)).toBe("/products/123")
      })

      it("should return correct path for string id", () => {
        expect(API_ENDPOINTS.PRODUCT_BY_ID("456")).toBe("/products/456")
      })
    })

    describe("PRODUCT_BY_BARCODE function", () => {
      it("should return correct path for barcode", () => {
        expect(API_ENDPOINTS.PRODUCT_BY_BARCODE("123456789")).toBe("/products/barcode/123456789")
      })
    })

    describe("PRODUCT_BY_SKU function", () => {
      it("should return correct path for SKU", () => {
        expect(API_ENDPOINTS.PRODUCT_BY_SKU("SKU001")).toBe("/products/sku/SKU001")
      })
    })

    describe("CATEGORY_BY_ID function", () => {
      it("should return correct path for category id", () => {
        expect(API_ENDPOINTS.CATEGORY_BY_ID(1)).toBe("/categories/1")
      })
    })

    describe("SUPPLIER endpoints", () => {
      it("should return correct base path", () => {
        expect(API_ENDPOINTS.SUPPLIERS).toBe("/suppliers")
      })

      it("should return correct path for supplier by id", () => {
        expect(API_ENDPOINTS.SUPPLIER_BY_ID(1)).toBe("/suppliers/1")
      })

      it("should return correct path for supplier performance", () => {
        expect(API_ENDPOINTS.SUPPLIER_PERFORMANCE(1)).toBe("/suppliers/1/performance")
      })

      it("should return correct path for supplier orders", () => {
        expect(API_ENDPOINTS.SUPPLIER_ORDERS(1)).toBe("/suppliers/1/orders")
      })
    })

    describe("WAREHOUSE endpoints", () => {
      it("should return correct base path", () => {
        expect(API_ENDPOINTS.WAREHOUSES).toBe("/warehouses")
      })

      it("should return correct path for warehouse by id", () => {
        expect(API_ENDPOINTS.WAREHOUSE_BY_ID(1)).toBe("/warehouses/1")
      })

      it("should return correct path for warehouse stats", () => {
        expect(API_ENDPOINTS.WAREHOUSE_STATS(1)).toBe("/warehouses/1/stats")
      })
    })

    describe("STOCK endpoints", () => {
      it("should have base stock path", () => {
        expect(API_ENDPOINTS.STOCK).toBe("/stock")
      })

      it("should return correct path for stock by id", () => {
        expect(API_ENDPOINTS.STOCK_BY_ID(1)).toBe("/stock/1")
      })

      it("should have transfer and adjustment paths", () => {
        expect(API_ENDPOINTS.STOCK_TRANSFER).toBe("/stock/transfer")
        expect(API_ENDPOINTS.STOCK_ADJUSTMENT).toBe("/stock/adjustment")
      })
    })

    describe("CUSTOMER endpoints", () => {
      it("should return correct base and by-id paths", () => {
        expect(API_ENDPOINTS.CUSTOMERS).toBe("/customers")
        expect(API_ENDPOINTS.CUSTOMER_BY_ID(1)).toBe("/customers/1")
      })
    })

    describe("EMPLOYEE endpoints", () => {
      it("should return correct base and by-id paths", () => {
        expect(API_ENDPOINTS.EMPLOYEES).toBe("/employees")
        expect(API_ENDPOINTS.EMPLOYEE_BY_ID(1)).toBe("/employees/1")
      })
    })

    describe("DEPARTMENT endpoints", () => {
      it("should return correct base and by-id paths", () => {
        expect(API_ENDPOINTS.DEPARTMENTS).toBe("/departments")
        expect(API_ENDPOINTS.DEPARTMENT_BY_ID(1)).toBe("/departments/1")
      })
    })

    describe("PURCHASE_ORDER endpoints", () => {
      it("should return correct paths", () => {
        expect(API_ENDPOINTS.PURCHASE_ORDERS).toBe("/purchase-orders")
        expect(API_ENDPOINTS.PURCHASE_ORDER_BY_ID(1)).toBe("/purchase-orders/1")
        expect(API_ENDPOINTS.PURCHASE_ORDER_APPROVE(1)).toBe("/purchase-orders/1/approve")
        expect(API_ENDPOINTS.PURCHASE_ORDER_REJECT(1)).toBe("/purchase-orders/1/reject")
        expect(API_ENDPOINTS.PURCHASE_ORDER_RECEIVE(1)).toBe("/purchase-orders/1/receive")
      })
    })

    describe("SALES_ORDER endpoints", () => {
      it("should return correct paths", () => {
        expect(API_ENDPOINTS.SALES_ORDERS).toBe("/sales-orders")
        expect(API_ENDPOINTS.SALES_ORDER_BY_ID(1)).toBe("/sales-orders/1")
        expect(API_ENDPOINTS.SALES_ORDER_CONFIRM(1)).toBe("/sales-orders/1/confirm")
        expect(API_ENDPOINTS.SALES_ORDER_CANCEL(1)).toBe("/sales-orders/1/cancel")
      })
    })

    describe("SHIPMENT endpoints", () => {
      it("should return correct paths", () => {
        expect(API_ENDPOINTS.SHIPMENTS).toBe("/shipments")
        expect(API_ENDPOINTS.SHIPMENT_BY_ID(1)).toBe("/shipments/1")
        expect(API_ENDPOINTS.SHIPMENT_BY_TRACKING("TRACK123")).toBe("/shipments/tracking/TRACK123")
        expect(API_ENDPOINTS.SHIPMENT_DELIVER(1)).toBe("/shipments/1/deliver")
        expect(API_ENDPOINTS.SHIPMENT_UPDATE_STATUS(1)).toBe("/shipments/1/status")
      })
    })

    describe("PAYMENT endpoints", () => {
      it("should return correct paths", () => {
        expect(API_ENDPOINTS.PAYMENTS).toBe("/payments")
        expect(API_ENDPOINTS.PAYMENT_BY_ID(1)).toBe("/payments/1")
      })
    })

    describe("INVOICE endpoints", () => {
      it("should return correct paths", () => {
        expect(API_ENDPOINTS.INVOICES).toBe("/invoices")
        expect(API_ENDPOINTS.INVOICE_BY_ID(1)).toBe("/invoices/1")
        expect(API_ENDPOINTS.INVOICE_DOWNLOAD(1)).toBe("/invoices/1/download")
      })
    })

    describe("DASHBOARD endpoints", () => {
      it("should have all dashboard endpoints", () => {
        expect(API_ENDPOINTS.DASHBOARD.OVERVIEW).toBe("/dashboard/overview")
        expect(API_ENDPOINTS.DASHBOARD.SALES_ANALYTICS).toBe("/dashboard/sales-analytics")
        expect(API_ENDPOINTS.DASHBOARD.PURCHASE_ANALYTICS).toBe("/dashboard/purchase-analytics")
        expect(API_ENDPOINTS.DASHBOARD.INVENTORY_SUMMARY).toBe("/dashboard/inventory-summary")
        expect(API_ENDPOINTS.DASHBOARD.INVENTORY_TREND).toBe("/dashboard/inventory-trend")
        expect(API_ENDPOINTS.DASHBOARD.SALES_TREND).toBe("/dashboard/sales-trend")
        expect(API_ENDPOINTS.DASHBOARD.TOP_SELLING_PRODUCTS).toBe("/dashboard/top-selling-products")
        expect(API_ENDPOINTS.DASHBOARD.LOW_STOCK_ALERTS).toBe("/dashboard/low-stock-alerts")
        expect(API_ENDPOINTS.DASHBOARD.PENDING_ACTIONS).toBe("/dashboard/pending-actions")
        expect(API_ENDPOINTS.DASHBOARD.ACTIVITY_FEED).toBe("/dashboard/activity-feed")
      })
    })

    describe("REPORTS endpoints", () => {
      it("should have report endpoints", () => {
        expect(API_ENDPOINTS.REPORTS.BASE).toBe("/reports")
        expect(API_ENDPOINTS.REPORTS.TYPES).toBe("/reports/types")
        expect(API_ENDPOINTS.REPORTS.GENERATE).toBe("/reports/generate")
        expect(API_ENDPOINTS.REPORTS.BY_ID(1)).toBe("/reports/1")
        expect(API_ENDPOINTS.REPORTS.DOWNLOAD(1)).toBe("/reports/1/download")
        expect(API_ENDPOINTS.REPORTS.SCHEDULED).toBe("/reports/scheduled")
        expect(API_ENDPOINTS.REPORTS.SCHEDULE).toBe("/reports/schedule")
        expect(API_ENDPOINTS.REPORTS.SCHEDULE_BY_ID(1)).toBe("/reports/scheduled/1")
      })
    })

    describe("NOTIFICATIONS endpoints", () => {
      it("should have notification endpoints", () => {
        expect(API_ENDPOINTS.NOTIFICATIONS.BASE).toBe("/notifications")
        expect(API_ENDPOINTS.NOTIFICATIONS.UNREAD_COUNT).toBe("/notifications/unread")
        expect(API_ENDPOINTS.NOTIFICATIONS.MARK_READ(1)).toBe("/notifications/1/read")
        expect(API_ENDPOINTS.NOTIFICATIONS.MARK_ALL_READ).toBe("/notifications/read-all")
        expect(API_ENDPOINTS.NOTIFICATIONS.CLEAR_READ).toBe("/notifications/clear-read")
        expect(API_ENDPOINTS.NOTIFICATIONS.PREFERENCES).toBe("/notifications/preferences")
      })
    })

    describe("USER endpoints", () => {
      it("should have user endpoints", () => {
        expect(API_ENDPOINTS.USERS).toBe("/users")
        expect(API_ENDPOINTS.USER_BY_ID(1)).toBe("/users/1")
        expect(API_ENDPOINTS.USER_INVITE).toBe("/users/invite")
      })
    })

    describe("ROLE endpoints", () => {
      it("should have role endpoints", () => {
        expect(API_ENDPOINTS.ROLES).toBe("/roles")
        expect(API_ENDPOINTS.ROLE_BY_ID(1)).toBe("/roles/1")
      })
    })

    it("should have audit logs endpoint", () => {
      expect(API_ENDPOINTS.AUDIT_LOGS).toBe("/audit-logs")
    })
  })

  describe("PAGINATION_DEFAULTS", () => {
    it("should have correct default values", () => {
      expect(PAGINATION_DEFAULTS.DEFAULT_PAGE).toBe(0)
      expect(PAGINATION_DEFAULTS.DEFAULT_SIZE).toBe(10)
      expect(PAGINATION_DEFAULTS.MAX_SIZE).toBe(100)
    })

    it("should have page starting at 0", () => {
      expect(PAGINATION_DEFAULTS.DEFAULT_PAGE).toBeLessThan(PAGINATION_DEFAULTS.DEFAULT_SIZE)
    })

    it("should have max size greater than default size", () => {
      expect(PAGINATION_DEFAULTS.MAX_SIZE).toBeGreaterThan(PAGINATION_DEFAULTS.DEFAULT_SIZE)
    })
  })

  describe("TIME_RANGES", () => {
    it("should have all required time ranges", () => {
      expect(TIME_RANGES).toHaveLength(8)
    })

    it("should have correct time range values", () => {
      expect(TIME_RANGES[0]).toEqual({ value: "1d", label: "1D" })
      expect(TIME_RANGES[1]).toEqual({ value: "7d", label: "7D" })
      expect(TIME_RANGES[2]).toEqual({ value: "1m", label: "1M" })
      expect(TIME_RANGES[3]).toEqual({ value: "3m", label: "3M" })
      expect(TIME_RANGES[4]).toEqual({ value: "6m", label: "6M" })
      expect(TIME_RANGES[5]).toEqual({ value: "1y", label: "1Y" })
      expect(TIME_RANGES[6]).toEqual({ value: "3y", label: "3Y" })
      expect(TIME_RANGES[7]).toEqual({ value: "5y", label: "5Y" })
    })

    it("should have value and label for each range", () => {
      TIME_RANGES.forEach((range) => {
        expect(range).toHaveProperty("value")
        expect(range).toHaveProperty("label")
        expect(typeof range.value).toBe("string")
        expect(typeof range.label).toBe("string")
      })
    })
  })
})