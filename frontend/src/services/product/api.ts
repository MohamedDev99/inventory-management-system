import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { productSchema } from "@/lib/schemas/product"
import { inventoryItemSchema, inventoryMovementSchema, inventoryListResponseSchema } from "@/lib/schemas/inventory"
import { createProductRequestSchema, updateProductRequestSchema } from "@/lib/schemas/product/request"
import type { PageParams } from "@/types"

// GET /api/products - Paginated product list
export async function getProducts(params: PageParams = {}) {
  const response = await api.get("/products", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(productSchema)),
    response.data,
    { prefix: "Get Products Response" }
  )
  return validated
}

// GET /api/products/{id} - Full product detail
export async function getProduct(id: number) {
  const response = await api.get(`/products/${id}`)
  const validated = validateResponse(
    apiResponseSchema(productSchema),
    response.data,
    { prefix: "Get Product Response" }
  )
  return validated
}

// GET /api/products/sku/{sku} - Lookup by SKU
export async function getProductBySku(sku: string) {
  const response = await api.get(`/products/sku/${sku}`)
  const validated = validateResponse(
    apiResponseSchema(productSchema),
    response.data,
    { prefix: "Get Product by SKU Response" }
  )
  return validated
}

// GET /api/products/barcode/{barcode} - Lookup by barcode
export async function getProductByBarcode(barcode: string) {
  const response = await api.get(`/products/barcode/${barcode}`)
  const validated = validateResponse(
    apiResponseSchema(productSchema),
    response.data,
    { prefix: "Get Product by Barcode Response" }
  )
  return validated
}

// GET /api/products/low-stock - Products at or below reorder level
export async function getLowStockProducts() {
  const response = await api.get("/products/low-stock")
  const validated = validateResponse(
    apiResponseSchema(z => z.array(productSchema)),
    response.data,
    { prefix: "Get Low Stock Products Response" }
  )
  return validated
}

// GET /api/products/critical-stock - Products at minimum stock level
export async function getCriticalStockProducts() {
  const response = await api.get("/products/critical-stock")
  const validated = validateResponse(
    apiResponseSchema(z => z.array(productSchema)),
    response.data,
    { prefix: "Get Critical Stock Products Response" }
  )
  return validated
}

// GET /api/products/out-of-stock - Products with zero stock
export async function getOutOfStockProducts() {
  const response = await api.get("/products/out-of-stock")
  const validated = validateResponse(
    apiResponseSchema(z => z.array(productSchema)),
    response.data,
    { prefix: "Get Out of Stock Products Response" }
  )
  return validated
}

// GET /api/products/stats/total-value - Total inventory value
export async function getTotalInventoryValue() {
  const response = await api.get("/products/stats/total-value")
  const validated = validateResponse(
    apiResponseSchema(z => z.number()),
    response.data,
    { prefix: "Get Total Inventory Value Response" }
  )
  return validated
}

// GET /api/products/stats/count - Total product count
export async function getProductCount(isActive?: boolean) {
  const response = await api.get("/products/stats/count", { params: { isActive } })
  const validated = validateResponse(
    apiResponseSchema(z => z.number()),
    response.data,
    { prefix: "Get Product Count Response" }
  )
  return validated
}

// POST /api/products - Create new product
export async function createProduct(data: Parameters<typeof createProductRequestSchema.parse>[0]) {
  const validatedData = createProductRequestSchema.parse(data)
  const response = await api.post("/products", validatedData)
  const validated = validateResponse(
    apiResponseSchema(productSchema),
    response.data,
    { prefix: "Create Product Response" }
  )
  return validated
}

// PUT /api/products/{id} - Full product update
export async function updateProduct(id: number, data: Parameters<typeof updateProductRequestSchema.parse>[0]) {
  const validatedData = updateProductRequestSchema.parse(data)
  const response = await api.put(`/products/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(productSchema),
    response.data,
    { prefix: "Update Product Response" }
  )
  return validated
}

// DELETE /api/products/{id} - Soft-delete product
export async function deleteProduct(id: number): Promise<void> {
  await api.delete(`/products/${id}`)
}

// POST /api/products/{id}/image - Upload product image
export async function uploadProductImage(productId: number, formData: FormData) {
  const response = await api.post(`/products/${productId}/image`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  })
  const validated = validateResponse(
    z => z.object({ imageUrl: z.string() }),
    response.data,
    { prefix: "Upload Product Image Response" }
  )
  return validated
}

// GET /api/products/{id}/inventory - Get product inventory
export async function getProductInventory(productId: number, params: PageParams = {}) {
  const response = await api.get(`/products/${productId}/inventory`, { params })
  const validated = validateResponse(
    apiResponseSchema(inventoryListResponseSchema),
    response.data,
    { prefix: "Get Product Inventory Response" }
  )
  return validated
}

// GET /api/products/{id}/movements - Get product movements
export async function getProductMovements(productId: number, params: PageParams = {}) {
  const response = await api.get(`/products/${productId}/movements`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(inventoryMovementSchema)),
    response.data,
    { prefix: "Get Product Movements Response" }
  )
  return validated
}
