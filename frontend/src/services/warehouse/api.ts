import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { warehouseSchema, warehouseStatsSchema } from "@/lib/schemas/warehouse"
import { inventoryItemSchema, inventoryListResponseSchema } from "@/lib/schemas/inventory"
import { createWarehouseRequestSchema, updateWarehouseRequestSchema } from "@/lib/schemas/warehouse/request"
import type { PageParams } from "@/types"

// GET /api/warehouses - Paginated list
export async function getWarehouses(params: PageParams = {}) {
  const response = await api.get("/warehouses", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(warehouseSchema)),
    response.data,
    { prefix: "Get Warehouses Response" }
  )
  return validated
}

// GET /api/warehouses/{id} - Full warehouse details
export async function getWarehouse(id: number) {
  const response = await api.get(`/warehouses/${id}`)
  const validated = validateResponse(
    apiResponseSchema(warehouseSchema),
    response.data,
    { prefix: "Get Warehouse Response" }
  )
  return validated
}

// GET /api/warehouses/code/{code} - Lookup by code
export async function getWarehouseByCode(code: string) {
  const response = await api.get(`/warehouses/code/${code}`)
  const validated = validateResponse(
    apiResponseSchema(warehouseSchema),
    response.data,
    { prefix: "Get Warehouse by Code Response" }
  )
  return validated
}

// GET /api/warehouses/active - All active warehouses (flat list)
export async function getActiveWarehouses() {
  const response = await api.get("/warehouses/active")
  const validated = validateResponse(
    apiResponseSchema(z.array(warehouseSchema)),
    response.data,
    { prefix: "Get Active Warehouses Response" }
  )
  return validated
}

// GET /api/warehouses/manager/{managerId} - Warehouses by manager
export async function getWarehousesByManager(managerId: number, params: PageParams = {}) {
  const response = await api.get(`/warehouses/manager/${managerId}`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(warehouseSchema)),
    response.data,
    { prefix: "Get Warehouses by Manager Response" }
  )
  return validated
}

// GET /api/warehouses/{id}/stats - Capacity utilization, product count, stock units, total value
export async function getWarehouseStats(warehouseId: number) {
  const response = await api.get(`/warehouses/${warehouseId}/stats`)
  const validated = validateResponse(
    apiResponseSchema(warehouseStatsSchema),
    response.data,
    { prefix: "Get Warehouse Stats Response" }
  )
  return validated
}

// GET /api/warehouses/low-stock-alerts - Warehouses with low stock products
export async function getLowStockAlerts() {
  const response = await api.get("/warehouses/low-stock-alerts")
  const validated = validateResponse(
    apiResponseSchema(z.array(warehouseSchema)),
    response.data,
    { prefix: "Get Low Stock Alerts Response" }
  )
  return validated
}

// GET /api/warehouses/stats/count - Total warehouse count
export async function getWarehouseCount(isActive?: boolean) {
  const response = await api.get("/warehouses/stats/count", { params: { isActive } })
  const validated = validateResponse(
    apiResponseSchema(z => z.number()),
    response.data,
    { prefix: "Get Warehouse Count Response" }
  )
  return validated
}

// POST /api/warehouses - Create new warehouse
export async function createWarehouse(data: Parameters<typeof createWarehouseRequestSchema.parse>[0]) {
  const validatedData = createWarehouseRequestSchema.parse(data)
  const response = await api.post("/warehouses", validatedData)
  const validated = validateResponse(
    apiResponseSchema(warehouseSchema),
    response.data,
    { prefix: "Create Warehouse Response" }
  )
  return validated
}

// PUT /api/warehouses/{id} - Full update
export async function updateWarehouse(id: number, data: Parameters<typeof updateWarehouseRequestSchema.parse>[0]) {
  const validatedData = updateWarehouseRequestSchema.parse(data)
  const response = await api.put(`/warehouses/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(warehouseSchema),
    response.data,
    { prefix: "Update Warehouse Response" }
  )
  return validated
}

// PATCH /api/warehouses/{id} - Partial update
export async function patchWarehouse(id: number, data: Parameters<typeof updateWarehouseRequestSchema.parse>[0]) {
  const validatedData = updateWarehouseRequestSchema.parse(data)
  const response = await api.patch(`/warehouses/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(warehouseSchema),
    response.data,
    { prefix: "Patch Warehouse Response" }
  )
  return validated
}

// DELETE /api/warehouses/{id} - Soft-delete (isActive = false)
export async function deleteWarehouse(id: number): Promise<void> {
  await api.delete(`/warehouses/${id}`)
}

// GET /api/warehouses/{id}/inventory - Get warehouse inventory
export async function getWarehouseInventory(warehouseId: number, params: PageParams = {}) {
  const response = await api.get(`/warehouses/${warehouseId}/inventory`, { params })
  const validated = validateResponse(
    apiResponseSchema(inventoryListResponseSchema),
    response.data,
    { prefix: "Get Warehouse Inventory Response" }
  )
  return validated
}
