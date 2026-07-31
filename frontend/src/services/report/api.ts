import { z } from "zod"
import api from "@/api/axios"
import { validateResponse } from "@/lib/utils/validation"
import { apiResponseSchema, paginatedResponseSchema } from "@/lib/schemas/common/api"
import { reportGenerationResponseSchema } from "@/lib/schemas/report"
import type { PageParams } from "@/types"

export interface ReportParams extends PageParams {
  reportType?: string
  status?: "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED"
  generatedBy?: number
  startDate?: string
  endDate?: string
}

// Request types
export type GenerateReportRequest = {
  reportType: string
  name: string
  description?: string
  parameters?: Record<string, unknown>
  format: "PDF" | "EXCEL" | "CSV"
}

export type ScheduleReportRequest = {
  reportType: string
  name: string
  parameters?: Record<string, unknown>
  schedule: {
    frequency: "DAILY" | "WEEKLY" | "MONTHLY"
    dayOfWeek?: string
    dayOfMonth?: number
    time: string
  }
  recipients: string[]
  format: "PDF" | "EXCEL" | "CSV"
  enabled?: boolean
}

// GET /api/reports - Paginated list
export async function getReports(params: ReportParams = {}) {
  const response = await api.get("/reports", { params })
  const validated = validateResponse(
    apiResponseSchema(paginatedResponseSchema(reportGenerationResponseSchema)),
    response.data,
    { prefix: "Get Reports Response" }
  )
  return validated
}

// GET /api/reports/{id} - Report detail
export async function getReport(id: number) {
  const response = await api.get(`/reports/${id}`)
  const validated = validateResponse(
    apiResponseSchema(reportGenerationResponseSchema),
    response.data,
    { prefix: "Get Report Response" }
  )
  return validated
}

// GET /api/reports/types - Available report types
export async function getReportTypes() {
  const response = await api.get("/reports/types")
  // Return raw response for complex metadata
  return response.data
}

// POST /api/reports/generate - Generate new report
export async function generateReport(data: GenerateReportRequest) {
  const response = await api.post("/reports/generate", data)
  const validated = validateResponse(
    apiResponseSchema(reportGenerationResponseSchema),
    response.data,
    { prefix: "Generate Report Response" }
  )
  return validated
}

// GET /api/reports/{id}/download - Download report
export async function downloadReport(id: number): Promise<Blob> {
  const response = await api.get(`/reports/${id}/download`, { responseType: 'blob' })
  return response.data
}

// DELETE /api/reports/{id} - Delete report
export async function deleteReport(id: number): Promise<void> {
  await api.delete(`/reports/${id}`)
}

// GET /api/reports/scheduled - List scheduled reports
export async function getScheduledReports() {
  const response = await api.get("/reports/scheduled")
  // Return raw response for complex nested objects
  return response.data
}

// POST /api/reports/schedule - Create scheduled report
export async function scheduleReport(data: ScheduleReportRequest) {
  const response = await api.post("/reports/schedule", data)
  // Return raw response
  return response.data
}

// DELETE /api/reports/scheduled/{id} - Delete scheduled report
export async function deleteScheduledReport(id: number): Promise<void> {
  await api.delete(`/reports/scheduled/${id}`)
}
