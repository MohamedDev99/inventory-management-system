import { useReducer } from "react"
import {
  usePurchaseOrders as usePurchaseOrdersQuery,
  useCreatePurchaseOrder,
  useDeletePurchaseOrder,
  useSubmitPurchaseOrder,
  useApprovePurchaseOrder,
  useRejectPurchaseOrder,
  useReceivePurchaseOrder,
} from "@/services/purchase-order"
import type { PurchaseOrder } from "@/types"
import type { CreatePurchaseOrderRequest } from "@/lib/schemas/purchase-order/request"

type TimeFilter = "1d" | "7d" | "1m" | "3m" | "6m" | "1y" | "3y" | "5y"

interface PendingOrderData {
  supplierId: number
  warehouseId: number
  items: { itemId: number; quantityReceived: number }[]
}

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

// Consolidate alert/modal state
interface AlertState {
  isCreateModalOpen: boolean
  isConfirmAlertOpen: boolean
  pendingOrderData: PendingOrderData | null
}

type AlertAction =
  | { type: "OPEN_CREATE" }
  | { type: "CLOSE_CREATE" }
  | { type: "OPEN_CONFIRM"; payload: PendingOrderData }
  | { type: "CLOSE_CONFIRM" }

function alertReducer(state: AlertState, action: AlertAction): AlertState {
  switch (action.type) {
    case "OPEN_CREATE":
      return { ...state, isCreateModalOpen: true }
    case "CLOSE_CREATE":
      return { ...state, isCreateModalOpen: false }
    case "OPEN_CONFIRM":
      return { ...state, isConfirmAlertOpen: true, pendingOrderData: action.payload }
    case "CLOSE_CONFIRM":
      return { ...state, isConfirmAlertOpen: false, pendingOrderData: null }
    default:
      return state
  }
}

interface UsePurchaseOrdersReturn {
  // State
  timeFilter: TimeFilter
  setTimeFilter: (value: TimeFilter) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  
  // Alert states
  isCreateModalOpen: boolean
  setIsCreateModalOpen: (value: boolean) => void
  isConfirmAlertOpen: boolean
  setIsConfirmAlertOpen: (value: boolean) => void
  pendingOrderData: PendingOrderData | null
  setPendingOrderData: (data: PendingOrderData | null) => void
  
  // Data
  orders: PurchaseOrder[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createPO: ReturnType<typeof useCreatePurchaseOrder>
  deletePO: ReturnType<typeof useDeletePurchaseOrder>
  submitPO: ReturnType<typeof useSubmitPurchaseOrder>
  approvePO: ReturnType<typeof useApprovePurchaseOrder>
  rejectPO: ReturnType<typeof useRejectPurchaseOrder>
  receivePO: ReturnType<typeof useReceivePurchaseOrder>
  
  // Handlers
  handleSearch: () => void
  handleCreateOrder: (data: CreatePurchaseOrderRequest) => void
  handleSubmit: (id: number) => void
  handleApprove: (id: number) => void
  handleReject: (id: number, reason: string) => void
  handleReceive: (id: number) => void
  handleConfirmReceive: () => void
  closeCreateModal: () => void
  closeConfirmAlert: () => void
}

export function usePurchaseOrders(): UsePurchaseOrdersReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })
  
  // Consolidated alert state
  const [alertState, dispatchAlert] = useReducer(alertReducer, {
    isCreateModalOpen: false,
    isConfirmAlertOpen: false,
    pendingOrderData: null,
  })
  
  // React Query hooks
  const { data: ordersData, isLoading } = usePurchaseOrdersQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createPO = useCreatePurchaseOrder()
  const deletePO = useDeletePurchaseOrder()
  const submitPO = useSubmitPurchaseOrder()
  const approvePO = useApprovePurchaseOrder()
  const rejectPO = useRejectPurchaseOrder()
  const receivePO = useReceivePurchaseOrder()
  
  const orders = ordersData?.data?.content || []
  const totalElements = ordersData?.data?.totalElements || 0
  const totalPages = ordersData?.data?.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilter) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Alert actions
  const setIsCreateModalOpen = (value: boolean) => value ? dispatchAlert({ type: "OPEN_CREATE" }) : dispatchAlert({ type: "CLOSE_CREATE" })
  const setIsConfirmAlertOpen = (value: boolean) => {
    if (value && alertState.pendingOrderData) {
      dispatchAlert({ type: "OPEN_CONFIRM", payload: alertState.pendingOrderData })
    } else {
      dispatchAlert({ type: "CLOSE_CONFIRM" })
    }
  }
  const setPendingOrderData = (data: PendingOrderData | null) => {
    if (data) {
      dispatchAlert({ type: "OPEN_CONFIRM", payload: data })
    }
  }
  const closeCreateModal = () => dispatchAlert({ type: "CLOSE_CREATE" })
  const closeConfirmAlert = () => dispatchAlert({ type: "CLOSE_CONFIRM" })
  
  // Mutation handlers - callbacks handle toasts
  const handleCreateOrder = (data: CreatePurchaseOrderRequest) => {
    createPO.mutate(data)
    closeCreateModal()
  }
  
  const handleSubmit = (id: number) => submitPO.mutate(id)
  const handleApprove = (id: number) => approvePO.mutate(id)
  const handleReject = (id: number, reason: string) => rejectPO.mutate({ id, reason })
  
  const handleReceive = () => {
    // Show confirmation alert
    dispatchAlert({
      type: "OPEN_CONFIRM",
      payload: { supplierId: 0, warehouseId: 0, items: [] },
    })
  }

  const handleConfirmReceive = () => {
    if (alertState.pendingOrderData) {
      receivePO.mutate({ id: 0, data: { items: alertState.pendingOrderData.items } })
      closeConfirmAlert()
    }
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
    
    // Alert states
    isCreateModalOpen: alertState.isCreateModalOpen,
    setIsCreateModalOpen,
    isConfirmAlertOpen: alertState.isConfirmAlertOpen,
    setIsConfirmAlertOpen,
    pendingOrderData: alertState.pendingOrderData,
    setPendingOrderData,
    
    // Data
    orders,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createPO,
    deletePO,
    submitPO,
    approvePO,
    rejectPO,
    receivePO,
    
    // Handlers
    handleSearch,
    handleCreateOrder,
    handleSubmit,
    handleApprove,
    handleReject,
    handleReceive,
    handleConfirmReceive,
    closeCreateModal,
    closeConfirmAlert,
  }
}
