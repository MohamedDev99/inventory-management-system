import { useReducer } from "react"
import {
  useInvoices as useInvoicesQuery,
  useCreateInvoice,
  useUpdateInvoiceStatus,
  useRecordInvoicePayment,
  useSendInvoice,
} from "@/services/invoice"
import type { Invoice } from "@/types"
import type { CreateInvoiceRequest } from "@/lib/schemas/invoice/request"
import type { UpdateInvoiceStatusRequest } from "@/lib/schemas/invoice/request"
import type { RecordInvoicePaymentRequest } from "@/services/invoice/api"

type TimeFilter = "1d" | "7d" | "1m" | "3m" | "6m" | "1y" | "3y" | "5y"

// Consolidate filter state
interface FilterState {
  timeFilter: TimeFilter
  searchQuery: string
  currentPage: number
  pageSize: number
}

type FilterAction =
  | { type: "SET_TIME_FILTER"; payload: TimeFilter }
  | { type: "SET_SEARCH_QUERY"; payload: string }
  | { type: "SET_CURRENT_PAGE"; payload: number }
  | { type: "RESET_PAGE" }

function filterReducer(state: FilterState, action: FilterAction): FilterState {
  switch (action.type) {
    case "SET_TIME_FILTER":
      return { ...state, timeFilter: action.payload }
    case "SET_SEARCH_QUERY":
      return { ...state, searchQuery: action.payload, currentPage: 0 }
    case "SET_CURRENT_PAGE":
      return { ...state, currentPage: action.payload }
    case "RESET_PAGE":
      return { ...state, currentPage: 0 }
    default:
      return state
  }
}

// Consolidate modal state
interface ModalState {
  isCreateModalOpen: boolean
}

type ModalAction =
  | { type: "OPEN_CREATE" }
  | { type: "CLOSE_CREATE" }

function modalReducer(state: ModalState, action: ModalAction): ModalState {
  switch (action.type) {
    case "OPEN_CREATE":
      return { ...state, isCreateModalOpen: true }
    case "CLOSE_CREATE":
      return { ...state, isCreateModalOpen: false }
    default:
      return state
  }
}

interface UseInvoicesReturn {
  // State
  timeFilter: TimeFilter
  setTimeFilter: (value: TimeFilter) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  
  // Modal state
  isCreateModalOpen: boolean
  setIsCreateModalOpen: (open: boolean) => void
  
  // Data
  invoices: Invoice[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createInvoice: ReturnType<typeof useCreateInvoice>
  updateStatus: ReturnType<typeof useUpdateInvoiceStatus>
  recordPayment: ReturnType<typeof useRecordInvoicePayment>
  sendInvoice: ReturnType<typeof useSendInvoice>
  
  // Handlers
  handleSearch: () => void
  handleCreate: (data: CreateInvoiceRequest) => void
  handleUpdateStatus: (id: number, status: UpdateInvoiceStatusRequest["status"]) => void
  handleRecordPayment: (id: number, data: RecordInvoicePaymentRequest) => void
  handleSend: (id: number) => void
  closeCreateModal: () => void
}

export function useInvoices(): UseInvoicesReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })
  
  // Consolidated modal state
  const [modalState, dispatchModal] = useReducer(modalReducer, {
    isCreateModalOpen: false,
  })
  
  // React Query hooks
  const { data: invoicesData, isLoading } = useInvoicesQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createInvoice = useCreateInvoice()
  const updateStatus = useUpdateInvoiceStatus()
  const recordPayment = useRecordInvoicePayment()
  const sendInvoice = useSendInvoice()
  
  const invoices = invoicesData?.data?.content || []
  const totalElements = invoicesData?.data?.totalElements || 0
  const totalPages = invoicesData?.data?.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilter) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Modal actions
  const setIsCreateModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_CREATE" }) : dispatchModal({ type: "CLOSE_CREATE" })
  const closeCreateModal = () => dispatchModal({ type: "CLOSE_CREATE" })
  
  // Mutation handlers - callbacks handle toasts
  const handleCreate = (data: CreateInvoiceRequest) => {
    createInvoice.mutate(data)
    closeCreateModal()
  }
  
  const handleUpdateStatus = (id: number, status: UpdateInvoiceStatusRequest["status"]) => {
    updateStatus.mutate({ id, status })
  }
  
  const handleRecordPayment = (id: number, data: RecordInvoicePaymentRequest) => {
    recordPayment.mutate({ id, data })
  }
  
  const handleSend = (id: number) => {
    sendInvoice.mutate(id)
  }
  
  return {
    // State
    timeFilter: filterState.timeFilter,
    setTimeFilter,
    searchQuery: filterState.searchQuery,
    setSearchQuery,
    currentPage: filterState.currentPage,
    setCurrentPage,
    pageSize: filterState.pageSize,
    
    // Modal state
    isCreateModalOpen: modalState.isCreateModalOpen,
    setIsCreateModalOpen,
    
    // Data
    invoices,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createInvoice,
    updateStatus,
    recordPayment,
    sendInvoice,
    
    // Handlers
    handleSearch,
    handleCreate,
    handleUpdateStatus,
    handleRecordPayment,
    handleSend,
    closeCreateModal,
  }
}
