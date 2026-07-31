// ========================================
// BACKWARD COMPATIBILITY - RE-EXPORTS
// ========================================
// This file re-exports from the new schema structure for backward compatibility.
// New code should import directly from the new location:
// import { loginRequestSchema } from "@/lib/schemas/auth"

// Re-export from new schema structure
export * from "./schemas"

// Aliases for backward compatibility
// Auth
export { loginRequestSchema as loginSchema, type LoginRequest as LoginFormData } from "./schemas/auth/request"
export { registerRequestSchema as registerSchema, type RegisterRequest as RegisterFormData } from "./schemas/auth/request"
export { changePasswordRequestSchema as changePasswordSchema, type ChangePasswordRequest as ChangePasswordFormData } from "./schemas/auth/request"
export { onboardingRequestSchema as onboardingSchema, type OnboardingRequest as OnboardingFormData } from "./schemas/auth/request"

// Product
export { createProductRequestSchema as productSchema, type CreateProductRequest as ProductFormData } from "./schemas/product/request"

// Category
export { createCategoryRequestSchema as categorySchema, type CreateCategoryRequest as CategoryFormData } from "./schemas/category/request"

// Warehouse
export { createWarehouseRequestSchema as warehouseSchema, type CreateWarehouseRequest as WarehouseFormData } from "./schemas/warehouse/request"

// Supplier
export { createSupplierRequestSchema as supplierSchema, type CreateSupplierRequest as SupplierFormData } from "./schemas/supplier/request"

// Customer
export { createCustomerRequestSchema as customerSchema, type CreateCustomerRequest as CustomerFormData } from "./schemas/customer/request"

// Employee
export { createEmployeeRequestSchema as employeeSchema, type CreateEmployeeRequest as EmployeeFormData } from "./schemas/employee/request"

// Department
export { createDepartmentRequestSchema as departmentSchema, type CreateDepartmentRequest as DepartmentFormData } from "./schemas/department/request"

// Inventory
export { inventoryRequestSchema as inventorySchema, type InventoryRequest as InventoryFormData } from "./schemas/stock/request"
export { stockTransferRequestSchema as stockTransferSchema, type StockTransferRequest as StockTransferFormData } from "./schemas/stock/request"
export { stockAdjustmentRequestSchema as stockAdjustmentSchema, type StockAdjustmentRequest as StockAdjustmentFormData } from "./schemas/stock/request"

// Purchase Order
export { purchaseOrderItemRequestSchema as purchaseOrderItemSchema } from "./schemas/purchase-order/request"
export { createPurchaseOrderRequestSchema as purchaseOrderSchema, type CreatePurchaseOrderRequest as PurchaseOrderFormData } from "./schemas/purchase-order/request"

// Sales Order
export { salesOrderItemRequestSchema as salesOrderItemSchema } from "./schemas/sales-order/request"
export { createSalesOrderRequestSchema as salesOrderSchema, type CreateSalesOrderRequest as SalesOrderFormData } from "./schemas/sales-order/request"

// Payment
export { createPaymentRequestSchema as paymentSchema, type CreatePaymentRequest as PaymentFormData } from "./schemas/payment/request"

// Invoice
export { createInvoiceRequestSchema as invoiceSchema, type CreateInvoiceRequest as InvoiceFormData } from "./schemas/invoice/request"

// Report
export { reportGenerationRequestSchema as reportGenerationSchema, type ReportGenerationRequest as ReportGenerationFormData } from "./schemas/report/request"

// Settings
export { profileSettingsRequestSchema as profileSettingsSchema, type ProfileSettingsRequest as ProfileSettingsFormData } from "./schemas/settings/request"
export { securitySettingsRequestSchema as securitySettingsSchema, type SecuritySettingsRequest as SecuritySettingsFormData } from "./schemas/settings/request"
export { businessSettingsRequestSchema as businessSettingsSchema, type BusinessSettingsRequest as BusinessSettingsFormData } from "./schemas/settings/request"

// Pagination
export { pageParamsSchema, type PageParamsSchema as PageParamsFormData } from "./schemas/common/api"
