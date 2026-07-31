import { useState, useEffect, useRef } from "react"
import toast from "react-hot-toast"
import api from "@/api/axios"
import {
  useReports as useReportsQuery,
  useGenerateReport,
  useDeleteReport,
} from "@/services/report"
import type { Report } from "@/types"

type ReportFormat = "PDF" | "EXCEL" | "CSV"

type TimeFilter = "1d" | "7d" | "1m" | "3m" | "6m" | "1y" | "3y" | "5y"

const reportTypesList = [
  { id: "STOCK_VALUATION", name: "Stock Valuation Report", desc: "Complete inventory valuation by warehouse and category", formats: ["PDF", "EXCEL", "CSV"] as const },
  { id: "INVENTORY_MOVEMENT", name: "Inventory Movement History", desc: "Detailed history of all inventory movements", formats: ["PDF", "EXCEL", "CSV"] as const },
  { id: "SALES_ANALYSIS", name: "Sales Analysis Report", desc: "Sales performance by product, customer, category", formats: ["PDF", "EXCEL"] as const },
  { id: "PURCHASE_HISTORY", name: "Purchase Order History", desc: "Complete purchase order history with supplier metrics", formats: ["PDF", "EXCEL"] as const },
  { id: "LOW_STOCK_ALERT", name: "Low Stock Alert Report", desc: "Products below reorder level with restock suggestions", formats: ["PDF", "EXCEL", "CSV"] as const },
]


interface GenerateFormState {
  name: string
  format: ReportFormat
  startDate: string
  endDate: string
}

interface UseReportsReturn {
  // State
  activeTab: "generate" | "history"
  setActiveTab: (tab: "generate" | "history") => void
  timeFilter: TimeFilter
  setTimeFilter: (value: TimeFilter) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  
  // Modal states
  isGenerateModalOpen: boolean
  setIsGenerateModalOpen: (value: boolean) => void
  selectedReportType: typeof reportTypesList[0] | null
  setSelectedReportType: (type: typeof reportTypesList[0] | null) => void
  generateForm: GenerateFormState
  setGenerateForm: React.Dispatch<React.SetStateAction<GenerateFormState>>
  isScheduledDrawerOpen: boolean
  setIsScheduledDrawerOpen: (value: boolean) => void
  
  // Data
  reports: Report[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  pendingReports: Report[]
  
  // Mutations
  generateReport: ReturnType<typeof useGenerateReport>
  deleteReport: ReturnType<typeof useDeleteReport>
  
  // Handlers
  handleSearch: () => void
  handleGenerateReport: () => void
  handleDownload: (report: Report) => void
  handleDelete: (reportId: number) => void
  
  // Constants
  reportTypes: typeof reportTypesList
  timeFilters: TimeFilter[]
}

export function useReports(): UseReportsReturn {
  // State
  const [activeTab, setActiveTab] = useState<"generate" | "history">("generate")
  const [timeFilter, setTimeFilter] = useState<TimeFilter>("1m")
  const [searchQuery, setSearchQuery] = useState("")
  const [currentPage, setCurrentPage] = useState(0)
  const [pageSize] = useState(10)
  const [isGenerateModalOpen, setIsGenerateModalOpen] = useState(false)
  const [selectedReportType, setSelectedReportType] = useState<typeof reportTypesList[0] | null>(null)
  const [generateForm, setGenerateForm] = useState<GenerateFormState>({ name: "", format: "PDF", startDate: "", endDate: "" })
  const [isScheduledDrawerOpen, setIsScheduledDrawerOpen] = useState(false)
  
  // Pending reports that need polling
  const [pendingReports, setPendingReports] = useState<Report[]>([])
  const pollingIntervalRef = useRef<number | null>(null)
  
  // React Query hooks
  const { data: reportsData, isLoading, refetch } = useReportsQuery({
    page: currentPage,
    size: pageSize,
    sort: "createdAt,desc",
    search: searchQuery,
  })
  
  const generateReportMutation = useGenerateReport()
  const deleteReportMutation = useDeleteReport()
  
  const reports = reportsData?.data?.content || []
  const totalElements = reportsData?.data?.totalElements || 0
  const totalPages = reportsData?.data?.totalPages || 0
  
  // Poll pending reports every 5 seconds
  useEffect(() => {
    if (pendingReports.length === 0) {
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current)
        pollingIntervalRef.current = null
      }
      return
    }
    
    pollingIntervalRef.current = window.setInterval(async () => {
      for (const report of pendingReports) {
        try {
          const response = await api.get(`/reports/${report.id}`)
          const updatedReport = response.data.data
          
          if (updatedReport.status === "COMPLETED") {
            toast.success("Report ready - click to download")
            setPendingReports((prev) => prev.filter((r) => r.id !== report.id))
            refetch()
          } else if (updatedReport.status === "FAILED") {
            toast.error("Report generation failed. Please try again.")
            setPendingReports((prev) => prev.filter((r) => r.id !== report.id))
            refetch()
          }
        } catch (error) {
          console.error("Polling error:", error)
        }
      }
    }, 5000)
    
    return () => {
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current)
      }
    }
  }, [pendingReports, refetch])
  
  const handleSearch = () => setCurrentPage(0)
  
  const handleGenerateReport = () => {
    if (!selectedReportType) return
    generateReportMutation.mutate(
      {
        name: generateForm.name || `${selectedReportType.name} - ${new Date().toLocaleDateString()}`,
        reportType: selectedReportType.id,
        format: generateForm.format,
      },
      {
        onSuccess: (result) => {
          setPendingReports((prev) => [...prev, result.data])
          toast.success("Report generation started. You'll be notified when it's ready.")
          setIsGenerateModalOpen(false)
          setActiveTab("history")
        },
        onError: () => {
          toast.error("Failed to generate report")
        },
      }
    )
  }
  
  const handleDownload = (report: Report) => {
    api.get(`/reports/${report.id}/download`, { responseType: "blob" })
      .then((response) => {
        const url = window.URL.createObjectURL(new Blob([response.data]))
        const link = document.createElement("a")
        link.href = url
        link.setAttribute("download", `${report.name}.${report.format.toLowerCase()}`)
        document.body.appendChild(link)
        link.click()
        link.remove()
        toast.success("Report downloaded")
      })
      .catch(() => {
        toast.error("Failed to download report")
      })
  }
  
  const handleDelete = (reportId: number) => {
    deleteReportMutation.mutate(reportId, {
      onSuccess: () => toast.success("Report deleted"),
      onError: () => toast.error("Failed to delete report"),
    })
  }
  
  
  
  const timeFilters: TimeFilter[] = ["1d", "7d", "1m", "3m", "6m", "1y", "3y", "5y"]
  
  return {
    // State
    activeTab,
    setActiveTab,
    timeFilter,
    setTimeFilter,
    searchQuery,
    setSearchQuery,
    currentPage,
    setCurrentPage,
    pageSize,
    
    // Modal states
    isGenerateModalOpen,
    setIsGenerateModalOpen,
    selectedReportType,
    setSelectedReportType,
    generateForm,
    setGenerateForm,
    isScheduledDrawerOpen,
    setIsScheduledDrawerOpen,
    
    // Data
    reports,
    totalElements,
    totalPages,
    isLoading,
    pendingReports,
    
    // Mutations
    generateReport: generateReportMutation,
    deleteReport: deleteReportMutation,
    
    // Handlers
    handleSearch,
    handleGenerateReport,
    handleDownload,
    handleDelete,
    
    // Constants
    reportTypes: reportTypesList,
    timeFilters,
  }
}
