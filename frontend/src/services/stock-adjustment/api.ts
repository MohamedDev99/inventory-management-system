import api from "@/api/axios"
import type { PaginatedResponse, PageParams, ApiResponse } from "@/types"

export interface StockAdjustment {
  id: number
  product: {
    id: number
    sku: string
    name: string
  }
  warehouse: {
    id: number
    name: string
  }
  quantityBefore: number
  quantityAfter: number
  quantityChange: number
  adjustmentType: "ADD" | "REMOVE" | "CORRECTION"
  reason: string
  status: "PENDING" | "APPROVED" | "REJECTED"
  notes?: string
  performedBy?: {
    id: number
    username: string
  }
  approvedBy?: {
    id: number
    username: string
  }
  adjustmentDate: string
  createdAt: string
}

export interface StockAdjustmentParams extends PageParams {
  productId?: number
  warehouseId?: number
  status?: "PENDING" | "APPROVED" | "REJECTED"
  adjustmentType?: "ADD" | "REMOVE" | "CORRECTION"
  reason?: string
  performedBy?: number
  startDate?: string
  endDate?: string
}

export async function getStockAdjustments(params: StockAdjustmentParams = {}): Promise<PaginatedResponse<StockAdjustment>> {
  const response = await api.get<ApiResponse<PaginatedResponse<StockAdjustment>>>("/stock-adjustments", { params })
  return response.data
}

export async function getStockAdjustment(id: number): Promise<StockAdjustment> {
  const response = await api.get<ApiResponse<StockAdjustment>>(`/stock-adjustments/${id}`)
  return response.data
}

export interface CreateStockAdjustmentRequest {
  productId: number
  warehouseId: number
  quantityChange: number
  adjustmentType: "ADD" | "REMOVE" | "CORRECTION"
  reason: string
  notes?: string
}

export async function createStockAdjustment(data: CreateStockAdjustmentRequest): Promise<StockAdjustment> {
  const response = await api.post<ApiResponse<StockAdjustment>>("/stock-adjustments", data)
  return response.data
}

export async function approveStockAdjustment(id: number, notes?: string): Promise<StockAdjustment> {
  const response = await api.patch<ApiResponse<StockAdjustment>>(`/stock-adjustments/${id}/approve`, { notes })
  return response.data
}

export async function rejectStockAdjustment(id: number, reason: string): Promise<StockAdjustment> {
  const response = await api.patch<ApiResponse<StockAdjustment>>(`/stock-adjustments/${id}/reject`, { reason })
  return response.data
}

export async function getPendingStockAdjustments(): Promise<StockAdjustment[]> {
  const response = await api.get<ApiResponse<StockAdjustment[]>>("/stock-adjustments/pending")
  return response.data
}

export async function getStockAdjustmentReasons(): Promise<string[]> {
  const response = await api.get<ApiResponse<string[]>>("/stock-adjustments/reasons")
  return response.data
}
