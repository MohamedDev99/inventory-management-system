import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { inventoryMovementSchema } from "@/lib/schemas/inventory"
import type { PageParams } from "@/types"

export interface InventoryMovementParams extends PageParams {
  productId?: number
  fromWarehouseId?: number
  toWarehouseId?: number
  movementType?: string
  performedBy?: number
  startDate?: string
  endDate?: string
  referenceNumber?: string
}

// GET /api/inventory-movements - Paginated list
export async function getInventoryMovements(params: InventoryMovementParams = {}) {
  const response = await api.get("/inventory-movements", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(inventoryMovementSchema)),
    response.data,
    { prefix: "Get Inventory Movements Response" }
  )
  return validated
}

// GET /api/inventory-movements/{id} - Single movement
export async function getInventoryMovement(id: number) {
  const response = await api.get(`/inventory-movements/${id}`)
  const validated = validateResponse(
    apiResponseSchema(inventoryMovementSchema),
    response.data,
    { prefix: "Get Inventory Movement Response" }
  )
  return validated
}

// GET /api/inventory-movements/product/{productId} - Movements by product
export async function getInventoryMovementsByProduct(productId: number, params: PageParams = {}) {
  const response = await api.get(`/inventory-movements/product/${productId}`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(inventoryMovementSchema)),
    response.data,
    { prefix: "Get Inventory Movements by Product Response" }
  )
  return validated
}

// GET /api/inventory-movements/warehouse/{warehouseId} - Movements by warehouse
export async function getInventoryMovementsByWarehouse(warehouseId: number, params: { direction?: "IN" | "OUT" | "BOTH" } = {}) {
  const response = await api.get(`/inventory-movements/warehouse/${warehouseId}`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(inventoryMovementSchema)),
    response.data,
    { prefix: "Get Inventory Movements by Warehouse Response" }
  )
  return validated
}

// GET /api/inventory-movements/summary - Movement summary
export async function getInventoryMovementSummary(params: {
  startDate?: string
  endDate?: string
  warehouseId?: number
  groupBy?: "DAY" | "WEEK" | "MONTH"
} = {}) {
  const response = await api.get("/inventory-movements/summary", { params })
  // Return raw response for aggregation data
  return response.data
}
