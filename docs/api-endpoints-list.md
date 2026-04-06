|-- Auth  [/api/auth]
|    |- POST   /api/auth/register
|    |    Creates new user account. Sets JWT access + refresh tokens as HttpOnly
|    |    cookies. Returns user info in body. 409 if username/email exists.
|    |
|    |- POST   /api/auth/login
|    |    Authenticates username + password. Sets JWT cookies on success. Locks
|    |    account for 30 min after 5 failed attempts. 423 if locked.
|    |
|    |- POST   /api/auth/refresh
|    |    Reads refresh_token cookie and issues a new access_token cookie.
|    |    No request body needed for browser clients.
|    |
|    |- POST   /api/auth/logout
|    |    Blacklists current access token in Redis, clears both JWT cookies.
|    |    Accepts cookie or Authorization: Bearer header.
|    |
|    |- POST   /api/auth/validate
|    |    Checks if the current access token is valid, not expired, and not
|    |    blacklisted. Returns boolean.
|    |
|    |- GET    /api/auth/me
|    |    Returns profile of the currently authenticated user.
|    |
|    |- POST   /api/auth/admin/reset-password/{userId}
|         Force-resets any user's password. ADMIN role only.
|
|
|-- Users  [/api/users]
|    |- GET    /api/users
|    |    Paginated list of all active users. ADMIN, MANAGER only.
|    |
|    |- GET    /api/users/{id}
|    |    Get user by ID. ADMIN/MANAGER can view any; other roles can only
|    |    view their own profile.
|    |
|    |- GET    /api/users/search?q=
|    |    Case-insensitive search by username or email. ADMIN, MANAGER only.
|    |
|    |- GET    /api/users/stats
|    |    Returns total users, active count, and counts per role. ADMIN only.
|    |
|    |- POST   /api/users
|    |    Admin-creates a new user with a specified role. Use /auth/register
|    |    for self-registration. ADMIN only.
|    |
|    |- PUT    /api/users/{id}
|    |    Updates user email or role. ADMIN can update any user. MANAGER can
|    |    only update their own profile (email only, no role change).
|    |
|    |- POST   /api/users/{id}/change-password
|    |    Changes own password with current password verification.
|    |    Only the user themselves can call this.
|    |
|    |- DELETE /api/users/{id}
|    |    Soft-deletes (deactivates) a user — user can no longer log in.
|    |    ADMIN only.
|    |
|    |- PATCH  /api/users/{id}/activate
|    |    Re-activates a previously deactivated user. ADMIN only.
|    |
|    |- PATCH  /api/users/{id}/unlock
|         Manually unlocks an account locked due to failed login attempts.
|         ADMIN only.
|
|
|-- Products  [/api/products]
|    |- GET    /api/products
|    |    Paginated list with filters: search, categoryId, isActive,
|    |    minPrice, maxPrice. All roles.
|    |
|    |- GET    /api/products/{id}
|    |    Full product details by ID. All roles.
|    |
|    |- GET    /api/products/sku/{sku}
|    |    Lookup product by its SKU code. All roles.
|    |
|    |- GET    /api/products/barcode/{barcode}
|    |    Lookup product by barcode (EAN, UPC, etc.). All roles.
|    |
|    |- GET    /api/products/category/{categoryId}
|    |    Paginated products belonging to a specific category. All roles.
|    |
|    |- GET    /api/products/low-stock
|    |    List of products at or below their reorder level.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- GET    /api/products/critical-stock
|    |    Products at or below minimum stock level (critical alert).
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- GET    /api/products/out-of-stock
|    |    Products with zero stock quantity. ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- GET    /api/products/stats/total-value
|    |    Total inventory value (cost price × quantity). ADMIN, MANAGER.
|    |
|    |- GET    /api/products/stats/count?isActive=
|    |    Total product count, optionally filtered by active status.
|    |    ADMIN, MANAGER, VIEWER.
|    |
|    |- POST   /api/products
|    |    Creates a new product in the catalog. ADMIN, MANAGER.
|    |
|    |- PUT    /api/products/{id}
|    |    Full update of an existing product. ADMIN, MANAGER.
|    |
|    |- DELETE /api/products/{id}
|         Soft-deletes product (sets isActive=false). Returns 204. ADMIN only.
|
|
|-- Categories  [/api/categories]
|    |- GET    /api/categories
|    |    Paginated list with filters: search, parentId, level. All roles.
|    |
|    |- GET    /api/categories/{id}
|    |    Category detail including parent and child counts. All roles.
|    |
|    |- GET    /api/categories/code/{code}
|    |    Lookup category by its unique code. All roles.
|    |
|    |- GET    /api/categories/tree
|    |    Full category hierarchy as a nested tree structure. All roles.
|    |
|    |- GET    /api/categories/{id}/subtree
|    |    Subtree starting from a specific category with all descendants.
|    |    All roles.
|    |
|    |- GET    /api/categories/roots
|    |    All top-level categories (no parent). All roles.
|    |
|    |- GET    /api/categories/{parentId}/children
|    |    Paginated child categories of a given parent. All roles.
|    |
|    |- GET    /api/categories/list
|    |    Flat ordered list of all categories for dropdowns. All roles.
|    |
|    |- GET    /api/categories/{id}/products
|    |    Total product count within this category and all subcategories.
|    |    All roles.
|    |
|    |- GET    /api/categories/empty
|    |    Categories with no products assigned. ADMIN, MANAGER.
|    |
|    |- POST   /api/categories
|    |    Creates a new category (root or child). ADMIN, MANAGER.
|    |
|    |- PUT    /api/categories/{id}
|    |    Full update. Validates against circular references when reparenting.
|    |    ADMIN, MANAGER.
|    |
|    |- PATCH  /api/categories/{id}
|    |    Partial update of specific fields. ADMIN, MANAGER.
|    |
|    |- DELETE /api/categories/{id}
|         Hard-delete. Fails if category has products or children. Returns 204.
|         ADMIN only.
|
|
|-- Warehouses  [/api/warehouses]
|    |- GET    /api/warehouses
|    |    Paginated list with filters: search, isActive, city, country, state.
|    |    All roles.
|    |
|    |- GET    /api/warehouses/{id}
|    |    Full warehouse details by ID. All roles.
|    |
|    |- GET    /api/warehouses/code/{code}
|    |    Lookup warehouse by its unique code. All roles.
|    |
|    |- GET    /api/warehouses/active
|    |    All active warehouses as a flat list (no pagination). All roles.
|    |
|    |- GET    /api/warehouses/manager/{managerId}
|    |    Paginated warehouses managed by a specific user. ADMIN, MANAGER.
|    |
|    |- GET    /api/warehouses/{id}/stats
|    |    Capacity utilization, product count, stock units, inventory value.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- GET    /api/warehouses/low-stock-alerts
|    |    Warehouses that have at least one product below reorder level.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- GET    /api/warehouses/stats/count?isActive=
|    |    Total warehouse count, optionally filtered by active status.
|    |    ADMIN, MANAGER, VIEWER.
|    |
|    |- POST   /api/warehouses
|    |    Creates a new warehouse. ADMIN only.
|    |
|    |- PUT    /api/warehouses/{id}
|    |    Full warehouse update. ADMIN only.
|    |
|    |- PATCH  /api/warehouses/{id}
|    |    Partial warehouse update. ADMIN only.
|    |
|    |- DELETE /api/warehouses/{id}
|         Soft-deletes warehouse (isActive=false). Returns 204. ADMIN only.
|
|
|-- Inventory  [/api/inventory]
|    |- GET    /api/inventory
|    |    Paginated inventory items with filters: warehouseId, productId,
|    |    lowStock flag, search. All roles.
|    |
|    |- GET    /api/inventory/{id}
|    |    Single inventory item detail by ID. All roles.
|    |
|    |- GET    /api/inventory/warehouse/{warehouseId}
|    |    Paginated inventory scoped to a specific warehouse. All roles.
|    |
|    |- GET    /api/inventory/movements
|    |    Movement history with filters: productId, warehouseId, movementType,
|    |    startDate, endDate. All roles.
|    |
|    |- GET    /api/inventory/valuation
|    |    Total inventory value filtered by warehouseId, categoryId, and
|    |    valuationType (COST or RETAIL). ADMIN, MANAGER, VIEWER.
|    |
|    |- POST   /api/inventory/transfer
|    |    Atomically transfers stock from one warehouse to another. Creates
|    |    movement records in both warehouses. ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- POST   /api/inventory/adjust
|    |    Creates a stock adjustment request. Returns 201.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- POST   /api/inventory/receive
|    |    Receives and processes incoming shipment from a purchase order,
|    |    updating inventory levels. ADMIN, MANAGER, WAREHOUSE_STAFF.
|
|
|-- Suppliers  [/api/suppliers]
|    |- GET    /api/suppliers
|    |    Paginated list with filters: isActive, country, minRating, maxRating,
|    |    search. Public (no @PreAuthorize).
|    |
|    |- GET    /api/suppliers/{id}
|    |    Full supplier detail by ID.
|    |
|    |- GET    /api/suppliers/code/{code}
|    |    Lookup supplier by unique code.
|    |
|    |- GET    /api/suppliers/top-rated
|    |    Suppliers sorted by rating descending (default top 10).
|    |
|    |- GET    /api/suppliers/search?term=
|    |    Searches suppliers by name, code, or email.
|    |
|    |- GET    /api/suppliers/count/active
|    |    Total count of active suppliers.
|    |
|    |- GET    /api/suppliers/{id}/orders
|    |    Purchase order history for a supplier with filters: status,
|    |    startDate, endDate.
|    |
|    |- GET    /api/suppliers/{id}/performance
|    |    Performance metrics: order stats, on-time delivery, recommendations.
|    |
|    |- POST   /api/suppliers
|    |    Creates a new supplier.
|    |
|    |- PUT    /api/suppliers/{id}
|    |    Full supplier update.
|    |
|    |- PATCH  /api/suppliers/{id}
|    |    Partial supplier update.
|    |
|    |- DELETE /api/suppliers/{id}
|         Soft-deletes supplier. Fails if supplier has pending orders. 409.
|
|
|-- Customers  [/api/customers]
|    |- GET    /api/customers
|    |    Paginated list with filters: customerType, isActive, city, country,
|    |    search. Public (no @PreAuthorize).
|    |
|    |- GET    /api/customers/{id}
|    |    Full customer detail by ID.
|    |
|    |- GET    /api/customers/code/{code}
|    |    Lookup customer by unique code.
|    |
|    |- GET    /api/customers/count/active
|    |    Total count of active customers.
|    |
|    |- GET    /api/customers/count/type/{type}
|    |    Customer count for a specific type (RETAIL, WHOLESALE, CORPORATE).
|    |
|    |- GET    /api/customers/{id}/orders
|    |    Paginated sales orders for a customer with filters: status,
|    |    startDate, endDate.
|    |
|    |- GET    /api/customers/{id}/invoices
|    |    Paginated invoices for a customer with filters: invoiceStatus,
|    |    startDate, endDate, overdue flag.
|    |
|    |- GET    /api/customers/{id}/payments
|    |    Paginated payment history with filters: paymentStatus, paymentMethod,
|    |    startDate, endDate.
|    |
|    |- GET    /api/customers/{id}/statement
|    |    Chronological ledger of invoices + payments with running balance,
|    |    credit limit, and totals. Filtered by startDate/endDate.
|    |
|    |- POST   /api/customers
|    |    Creates a new customer.
|    |
|    |- PUT    /api/customers/{id}
|    |    Full customer update.
|    |
|    |- PATCH  /api/customers/{id}
|    |    Partial customer update.
|    |
|    |- DELETE /api/customers/{id}
|         Soft-deletes customer.
|
|
|-- Purchase Orders  [/api/purchase-orders]
|    |- GET    /api/purchase-orders
|    |    Paginated list with filters: search, supplierId, warehouseId,
|    |    status, createdBy, startDate, endDate. All roles.
|    |
|    |- GET    /api/purchase-orders/{id}
|    |    Full PO detail including all line items. All roles.
|    |
|    |- GET    /api/purchase-orders/pending-approval
|    |    All POs in SUBMITTED status awaiting approval. ADMIN, MANAGER.
|    |
|    |- POST   /api/purchase-orders
|    |    Creates PO in DRAFT status. Auto-generates order number.
|    |    ADMIN, MANAGER.
|    |
|    |- PUT    /api/purchase-orders/{id}
|    |    Updates a PO — DRAFT status only. ADMIN, MANAGER.
|    |
|    |- POST   /api/purchase-orders/{id}/receive
|    |    Marks APPROVED PO as RECEIVED. Records actual quantities. Supports
|    |    partial receipts. ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- PATCH  /api/purchase-orders/{id}/submit
|    |    DRAFT → SUBMITTED for manager approval. ADMIN, MANAGER.
|    |
|    |- PATCH  /api/purchase-orders/{id}/approve
|    |    SUBMITTED → APPROVED. ADMIN, MANAGER.
|    |
|    |- PATCH  /api/purchase-orders/{id}/reject
|    |    SUBMITTED → DRAFT (returned for revision). Requires reason in body.
|    |    ADMIN, MANAGER.
|    |
|    |- PATCH  /api/purchase-orders/{id}/cancel
|    |    Cancels a PO (not allowed once RECEIVED). Requires reason.
|    |    ADMIN, MANAGER.
|    |
|    |- DELETE /api/purchase-orders/{id}
|         Permanently deletes a PO — DRAFT status only. Returns 204.
|         ADMIN, MANAGER.
|
|
|-- Sales Orders  [/api/sales-orders]
|    |- GET    /api/sales-orders
|    |    Paginated list with filters: search, customerId, warehouseId,
|    |    status, createdBy, startDate, endDate. All roles.
|    |
|    |- GET    /api/sales-orders/{id}
|    |    Full SO detail including line items and all workflow dates. All roles.
|    |
|    |- POST   /api/sales-orders
|    |    Creates SO in PENDING status. Auto-generates SO number.
|    |    ADMIN, MANAGER.
|    |
|    |- PUT    /api/sales-orders/{id}
|    |    Updates SO — PENDING status only. ADMIN, MANAGER.
|    |
|    |- PATCH  /api/sales-orders/{id}/confirm
|    |    PENDING → CONFIRMED. Reserves inventory in the warehouse.
|    |    ADMIN, MANAGER.
|    |
|    |- PATCH  /api/sales-orders/{id}/fulfill
|    |    CONFIRMED → FULFILLED. Deducts inventory, creates movement records.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- PATCH  /api/sales-orders/{id}/ship
|    |    FULFILLED → SHIPPED. Records shipping date.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- PATCH  /api/sales-orders/{id}/deliver
|    |    SHIPPED → DELIVERED. Records delivery date.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- PATCH  /api/sales-orders/{id}/cancel
|    |    Cancels SO (not allowed once SHIPPED or DELIVERED). Releases reserved
|    |    inventory. Requires reason + performedByUserId. ADMIN, MANAGER.
|
|
|-- Shipments  [/api/shipments]
|    |- GET    /api/shipments
|    |    Paginated list with filters: warehouseId, status, carrier. All roles.
|    |
|    |- GET    /api/shipments/{id}
|    |    Full shipment detail by internal ID. All roles.
|    |
|    |- GET    /api/shipments/tracking/{trackingNumber}
|    |    Lookup shipment by carrier tracking number. All roles.
|    |
|    |- GET    /api/shipments/sales-order/{salesOrderId}
|    |    All shipments linked to a specific sales order. All roles.
|    |
|    |- GET    /api/shipments/pending
|    |    All shipments with PENDING or IN_TRANSIT status.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- GET    /api/shipments/overdue
|    |    Shipments past estimated delivery date, not yet delivered.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- POST   /api/shipments
|    |    Creates a shipment for a FULFILLED sales order. Automatically
|    |    advances SO to SHIPPED. ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- PATCH  /api/shipments/{id}/status
|    |    Updates shipment status (e.g. PENDING → IN_TRANSIT).
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- PATCH  /api/shipments/{id}/deliver
|    |    Records actual delivery date. Sets shipment + linked SO to DELIVERED.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|
|
|-- Payments  [/api/payments]
|    |- GET    /api/payments
|    |    Paginated list with filters: search, customerId, salesOrderId,
|    |    paymentMethod, paymentStatus, startDate, endDate. ADMIN, MANAGER, VIEWER.
|    |
|    |- GET    /api/payments/{id}
|    |    Full payment detail by ID. ADMIN, MANAGER, VIEWER.
|    |
|    |- GET    /api/payments/methods
|    |    Returns list of all accepted payment methods. All roles.
|    |
|    |- POST   /api/payments
|    |    Records a new payment transaction. salesOrderId is optional
|    |    (supports advance/account credit payments). ADMIN, MANAGER.
|    |
|    |- PATCH  /api/payments/{id}/status
|    |    Updates payment status (e.g. PENDING → COMPLETED or FAILED).
|    |    ADMIN, MANAGER.
|    |
|    |- POST   /api/payments/{id}/refund
|    |    Processes a refund. Amount cannot exceed original payment.
|    |    ADMIN, MANAGER.
|
|
|-- Invoices  [/api/invoices]
|    |- GET    /api/invoices
|    |    Paginated list with filters: search, customerId, salesOrderId,
|    |    invoiceStatus, startDate, endDate, dueDate, overdue flag.
|    |    ADMIN, MANAGER, VIEWER.
|    |
|    |- GET    /api/invoices/{id}
|    |    Full invoice detail by ID. ADMIN, MANAGER, VIEWER.
|    |
|    |- GET    /api/invoices/overdue
|    |    All invoices past due date with outstanding balance.
|    |    ADMIN, MANAGER, VIEWER.
|    |
|    |- GET    /api/invoices/{id}/pdf
|    |    Downloads invoice as PDF. ⚠️ PDF generation is a TODO placeholder.
|    |    ADMIN, MANAGER, VIEWER.
|    |
|    |- POST   /api/invoices
|    |    Generates a new DRAFT invoice from a sales order. Only one invoice
|    |    per SO allowed. Returns 201. ADMIN, MANAGER.
|    |
|    |- POST   /api/invoices/{id}/send
|    |    Marks invoice as SENT and triggers email delivery to customer.
|    |    Cannot send if cancelled or already paid. ADMIN, MANAGER.
|    |
|    |- POST   /api/invoices/{id}/payment
|    |    Records a payment against an invoice. Auto-creates Payment record.
|    |    Sets status to PARTIAL or PAID based on remaining balance.
|    |    ADMIN, MANAGER.
|    |
|    |- PATCH  /api/invoices/{id}/status
|    |    Manually updates invoice status. If paidAmount provided, recalculates
|    |    balance and auto-sets PAID or PARTIAL. ADMIN, MANAGER.
|
|
|-- Departments  [/api/departments]
|    |- GET    /api/departments
|    |    Paginated list with filters: search, parentId, isActive. All roles.
|    |
|    |- GET    /api/departments/{id}
|    |    Department detail including manager, parent, and child counts.
|    |    All roles.
|    |
|    |- GET    /api/departments/{id}/employees
|    |    Paginated employees assigned to this department. All roles.
|    |
|    |- GET    /api/departments/{id}/stats
|    |    Aggregated stats: total employees, active employees, child depts.
|    |    All roles.
|    |
|    |- POST   /api/departments
|    |    Creates a new department. Returns 201. ADMIN only.
|    |
|    |- PUT    /api/departments/{id}
|    |    Full department update. ADMIN only.
|    |
|    |- DELETE /api/departments/{id}
|    |    Soft-deletes department (isActive=false). Data retained. ADMIN only.
|
|
|-- Employees  [/api/employees]
|    |- GET    /api/employees
|    |    Paginated list with filters: search, departmentId, managerId,
|    |    isActive. ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- GET    /api/employees/{id}
|    |    Full employee detail including manager, direct reports, department,
|    |    and linked system user. ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- GET    /api/employees/{id}/subordinates
|    |    Paginated direct reports of a given employee/manager.
|    |    ADMIN, MANAGER, WAREHOUSE_STAFF.
|    |
|    |- POST   /api/employees
|    |    Creates a new employee record. Returns 201. ADMIN, MANAGER.
|    |
|    |- POST   /api/employees/{id}/link-user
|    |    Links an employee to a system user account (grants system access).
|    |    ADMIN only.
|    |
|    |- PUT    /api/employees/{id}
|    |    Full employee update. ADMIN, MANAGER.
|    |
|    |- PATCH  /api/employees/{id}
|    |    Partial employee update. ADMIN, MANAGER.
|    |
|    |- PATCH  /api/employees/{id}/assign-department
|    |    Assigns or changes the employee's department. ADMIN, MANAGER.
|    |
|    |- PATCH  /api/employees/{id}/assign-manager
|    |    Sets or changes the employee's direct manager. Validates against
|    |    self-assignment. ADMIN, MANAGER.
|    |
|    |- DELETE /api/employees/{id}
|    |    Soft-deletes employee (isActive=false). Data retained. ADMIN only.
|    |
|    |- DELETE /api/employees/{id}/unlink-user
|         Removes link between employee and system user, revoking system
|         access. ADMIN only.