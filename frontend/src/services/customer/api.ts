import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { customerSchema, customerStatementSchema } from "@/lib/schemas/customer"
import { salesOrderSummarySchema } from "@/lib/schemas/sales-order"
import { invoiceSchema } from "@/lib/schemas/invoice"
import { paymentSchema } from "@/lib/schemas/payment"
import { createCustomerRequestSchema, updateCustomerRequestSchema } from "@/lib/schemas/customer/request"
import type { PageParams } from "@/types"

// GET /api/customers - Paginated list
export async function getCustomers(params: PageParams = {}) {
  const response = await api.get("/customers", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(customerSchema)),
    response.data,
    { prefix: "Get Customers Response" }
  )
  return validated
}

// GET /api/customers/{id} - Full customer detail
export async function getCustomer(id: number) {
  const response = await api.get(`/customers/${id}`)
  const validated = validateResponse(
    apiResponseSchema(customerSchema),
    response.data,
    { prefix: "Get Customer Response" }
  )
  return validated
}

// GET /api/customers/code/{code} - Lookup by code
export async function getCustomerByCode(code: string) {
  const response = await api.get(`/customers/code/${code}`)
  const validated = validateResponse(
    apiResponseSchema(customerSchema),
    response.data,
    { prefix: "Get Customer by Code Response" }
  )
  return validated
}

// GET /api/customers/count/active - Total active customer count
export async function getActiveCustomerCount() {
  const response = await api.get("/customers/count/active")
  const validated = validateResponse(
    apiResponseSchema(z => z.number()),
    response.data,
    { prefix: "Get Active Customer Count Response" }
  )
  return validated
}

// GET /api/customers/count/type/{type} - Count by customer type
export async function getCustomerCountByType(type: "RETAIL" | "WHOLESALE" | "CORPORATE") {
  const response = await api.get(`/customers/count/type/${type}`)
  const validated = validateResponse(
    apiResponseSchema(z => z.number()),
    response.data,
    { prefix: "Get Customer Count by Type Response" }
  )
  return validated
}

// GET /api/customers/{id}/orders - Sales orders for customer
export async function getCustomerOrders(customerId: number, params: PageParams = {}) {
  const response = await api.get(`/customers/${customerId}/orders`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(salesOrderSummarySchema)),
    response.data,
    { prefix: "Get Customer Orders Response" }
  )
  return validated
}

// GET /api/customers/{id}/invoices - Invoices for customer
export async function getCustomerInvoices(customerId: number, params: PageParams = {}) {
  const response = await api.get(`/customers/${customerId}/invoices`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(invoiceSchema)),
    response.data,
    { prefix: "Get Customer Invoices Response" }
  )
  return validated
}

// GET /api/customers/{id}/payments - Payment history
export async function getCustomerPayments(customerId: number, params: PageParams = {}) {
  const response = await api.get(`/customers/${customerId}/payments`, { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(paymentSchema)),
    response.data,
    { prefix: "Get Customer Payments Response" }
  )
  return validated
}

// GET /api/customers/{id}/statement - Full account statement
export async function getCustomerStatement(customerId: number, startDate?: string, endDate?: string) {
  const response = await api.get(`/customers/${customerId}/statement`, { params: { startDate, endDate } })
  const validated = validateResponse(
    apiResponseSchema(customerStatementSchema),
    response.data,
    { prefix: "Get Customer Statement Response" }
  )
  return validated
}

// POST /api/customers - Create new customer
export async function createCustomer(data: Parameters<typeof createCustomerRequestSchema.parse>[0]) {
  const validatedData = createCustomerRequestSchema.parse(data)
  const response = await api.post("/customers", validatedData)
  const validated = validateResponse(
    apiResponseSchema(customerSchema),
    response.data,
    { prefix: "Create Customer Response" }
  )
  return validated
}

// PUT /api/customers/{id} - Full update
export async function updateCustomer(id: number, data: Parameters<typeof updateCustomerRequestSchema.parse>[0]) {
  const validatedData = updateCustomerRequestSchema.parse(data)
  const response = await api.put(`/customers/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(customerSchema),
    response.data,
    { prefix: "Update Customer Response" }
  )
  return validated
}

// PATCH /api/customers/{id} - Partial update
export async function patchCustomer(id: number, data: Parameters<typeof updateCustomerRequestSchema.parse>[0]) {
  const validatedData = updateCustomerRequestSchema.parse(data)
  const response = await api.patch(`/customers/${id}`, validatedData)
  const validated = validateResponse(
    apiResponseSchema(customerSchema),
    response.data,
    { prefix: "Patch Customer Response" }
  )
  return validated
}

// DELETE /api/customers/{id} - Soft-delete
export async function deleteCustomer(id: number): Promise<void> {
  await api.delete(`/customers/${id}`)
}
