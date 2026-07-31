import { useReducer } from "react"
import {
  usePayments as usePaymentsQuery,
  useCreatePayment,
  useUpdatePaymentStatus,
  useRefundPayment,
} from "@/services/payment"
import type { Payment } from "@/types"
import type { CreatePaymentRequest } from "@/lib/schemas/payment/request"
import type { UpdatePaymentStatusRequest } from "@/services/payment/api"
import type { RefundPaymentRequest } from "@/services/payment/api"

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

interface UsePaymentsReturn {
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
  payments: Payment[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createPayment: ReturnType<typeof useCreatePayment>
  updateStatus: ReturnType<typeof useUpdatePaymentStatus>
  refundPayment: ReturnType<typeof useRefundPayment>
  
  // Handlers
  handleSearch: () => void
  handleCreate: (data: CreatePaymentRequest) => void
  handleUpdateStatus: (id: number, status: UpdatePaymentStatusRequest["status"], notes?: string) => void
  handleRefund: (id: number, data: RefundPaymentRequest) => void
  closeCreateModal: () => void
}

export function usePayments(): UsePaymentsReturn {
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
  const { data: paymentsData, isLoading } = usePaymentsQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createPayment = useCreatePayment()
  const updateStatus = useUpdatePaymentStatus()
  const refundPayment = useRefundPayment()
  
  const payments = paymentsData?.data?.content || []
  const totalElements = paymentsData?.data?.totalElements || 0
  const totalPages = paymentsData?.data?.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilter) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Modal actions
  const setIsCreateModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_CREATE" }) : dispatchModal({ type: "CLOSE_CREATE" })
  const closeCreateModal = () => dispatchModal({ type: "CLOSE_CREATE" })
  
  // Mutation handlers - callbacks handle toasts
  const handleCreate = (data: CreatePaymentRequest) => {
    createPayment.mutate(data)
    closeCreateModal()
  }
  
  const handleUpdateStatus = (id: number, status: UpdatePaymentStatusRequest["status"], notes?: string) => {
    updateStatus.mutate({ id, status, notes })
  }
  
  const handleRefund = (id: number, data: RefundPaymentRequest) => {
    refundPayment.mutate({ id, data })
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
    payments,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createPayment,
    updateStatus,
    refundPayment,
    
    // Handlers
    handleSearch,
    handleCreate,
    handleUpdateStatus,
    handleRefund,
    closeCreateModal,
  }
}
