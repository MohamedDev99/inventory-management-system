import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { inventoryItemSchema, inventoryMovementSchema, inventoryValuationSchema } from "@/lib/schemas/inventory"
import { stockTransferRequestSchema, stockAdjustmentRequestSchema } from "@/lib/schemas/stock/request"
import type { PageParams } from "@/types"

// GET /api/inventory - Paginated inventory items
export async function getInventory(params: PageParams & { warehouseId?: number; productId?: number; lowStock?: boolean; search?: string } = {}) {
  const response = await api.get("/inventory", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(inventoryItemSchema)),
    response.data,
    { prefix: "Get Inventory Response" }
  )
  return validated
}

// GET /api/inventory/{id} - Single inventory item
export async function getInventoryItem(id: number) {
  const response = await api.get(`/inventory/${id}`)
  const validated = validateResponse(
    apiResponseSchema(inventoryItemSchema),
    response.data,
    { prefix: "Get Inventory Item Response" }
  )
  return validated
}

// GET /api/inventory/warehouse/{warehouseId} - All inventory in a warehouse
export async function getInventoryByWarehouse(warehouseId: number, params: PageParams = {}) {
  const response = await api.get(`/inventory/warehouse/${warehouseId}`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(inventoryItemSchema)),
    response.data,
    { prefix: "Get Inventory by Warehouse Response" }
  )
  return validated
}

// GET /api/inventory/movements - Movement history
export async function getInventoryMovements(params: PageParams & {
  productId?: number
  warehouseId?: number
  movementType?: "TRANSFER" | "ADJUSTMENT" | "PURCHASE_RECEIPT" | "SALE"
  startDate?: string
  endDate?: string
} = {}) {
  const response = await api.get("/inventory/movements", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(inventoryMovementSchema)),
    response.data,
    { prefix: "Get Inventory Movements Response" }
  )
  return validated
}

// GET /api/inventory/valuation - Total inventory value
export async function getInventoryValuation(params: { warehouseId?: number; categoryId?: number; valuationType?: "COST" | "RETAIL" } = {}) {
  const response = await api.get("/inventory/valuation", { params })
  const validated = validateResponse(
    apiResponseSchema(inventoryValuationSchema),
    response.data,
    { prefix: "Get Inventory Valuation Response" }
  )
  return validated
}

// POST /api/inventory/transfer - Transfer stock between warehouses
export async function transferStock(data: Parameters<typeof stockTransferRequestSchema.parse>[0] & { performedBy: number }) {
  const validatedData = stockTransferRequestSchema.parse(data)
  const response = await api.post("/inventory/transfer", validatedData)
  const validated = validateResponse(
    apiResponseSchema(z => z.any()),
    response.data,
    { prefix: "Transfer Stock Response" }
  )
  return validated
}

// POST /api/inventory/adjust - Create stock adjustment
export async function adjustStock(data: Parameters<typeof stockAdjustmentRequestSchema.parse>[0] & { performedBy: number }) {
  const validatedData = stockAdjustmentRequestSchema.parse(data)
  const response = await api.post("/inventory/adjust", validatedData)
  const validated = validateResponse(
    apiResponseSchema(z => z.any()),
    response.data,
    { prefix: "Adjust Stock Response" }
  )
  return validated
}

// POST /api/inventory/receive - Receive shipment from PO
export async function receiveStock(data: {
  purchaseOrderId: number
  warehouseId: number
  performedBy?: number
  receivedDate?: string
  notes?: string
  items: { productId: number; quantityReceived: number; locationCode?: string }[]
}) {
  const response = await api.post("/inventory/receive", data)
  const validated = validateResponse(
    apiResponseSchema(z => z.any()),
    response.data,
    { prefix: "Receive Stock Response" }
  )
  return validated
}

// GET /api/inventory/product/{productId} - All inventory for a product across warehouses
export async function getInventoryByProduct(productId: number) {
  const response = await api.get(`/inventory/product/${productId}`)
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(inventoryItemSchema)),
    response.data,
    { prefix: "Get Inventory by Product Response" }
  )
  return validated
}

// POST /api/inventory - Create new inventory item
export async function createInventoryItem(data: {
  productId: number
  warehouseId: number
  quantity: number
  locationCode?: string
}) {
  const response = await api.post("/inventory", data)
  const validated = validateResponse(
    apiResponseSchema(inventoryItemSchema),
    response.data,
    { prefix: "Create Inventory Item Response" }
  )
  return validated
}

// PUT /api/inventory/{id} - Update inventory item
export async function updateInventoryItem(id: number, data: {
  quantity?: number
  locationCode?: string
  reorderPoint?: number
  reorderQuantity?: number
}) {
  const response = await api.put(`/inventory/${id}`, data)
  const validated = validateResponse(
    apiResponseSchema(inventoryItemSchema),
    response.data,
    { prefix: "Update Inventory Item Response" }
  )
  return validated
}
