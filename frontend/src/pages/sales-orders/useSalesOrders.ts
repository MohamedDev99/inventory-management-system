import { useReducer } from "react"
import {
  useSalesOrders as useSalesOrdersQuery,
  useCreateSalesOrder,
  useDeleteSalesOrder,
  useConfirmSalesOrder,
  useFulfillSalesOrder,
  useShipSalesOrder,
  useDeliverSalesOrder,
  useCancelSalesOrder,
} from "@/services/sales-order"
import type { SalesOrder } from "@/types"

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

interface UseSalesOrdersReturn {
  // State
  timeFilter: TimeFilter
  setTimeFilter: (value: TimeFilter) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  
  // Data
  orders: SalesOrder[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createSO: ReturnType<typeof useCreateSalesOrder>
  deleteSO: ReturnType<typeof useDeleteSalesOrder>
  confirmSO: ReturnType<typeof useConfirmSalesOrder>
  fulfillSO: ReturnType<typeof useFulfillSalesOrder>
  shipSO: ReturnType<typeof useShipSalesOrder>
  deliverSO: ReturnType<typeof useDeliverSalesOrder>
  cancelSO: ReturnType<typeof useCancelSalesOrder>
  
  // Handlers
  handleSearch: () => void
  handleConfirm: (id: number) => void
  handleFulfill: (id: number) => void
  handleShip: (id: number) => void
  handleDeliver: (id: number) => void
  handleCancel: (id: number, reason: string) => void
  handleDelete: (id: number) => void
}

export function useSalesOrders(): UseSalesOrdersReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })
  
  // React Query hooks
  const { data: ordersData, isLoading } = useSalesOrdersQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createSO = useCreateSalesOrder()
  const deleteSO = useDeleteSalesOrder()
  const confirmSO = useConfirmSalesOrder()
  const fulfillSO = useFulfillSalesOrder()
  const shipSO = useShipSalesOrder()
  const deliverSO = useDeliverSalesOrder()
  const cancelSO = useCancelSalesOrder()
  
  const orders = ordersData?.data?.content || []
  const totalElements = ordersData?.data?.totalElements || 0
  const totalPages = ordersData?.data?.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilter) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Mutation handlers - callbacks handle toasts
  const handleConfirm = (id: number) => confirmSO.mutate(id)
  const handleFulfill = (id: number) => fulfillSO.mutate(id)
  const handleShip = (id: number) => shipSO.mutate({ id })
  const handleDeliver = (id: number) => deliverSO.mutate(id)
  const handleCancel = (id: number, reason: string) => cancelSO.mutate({ id, reason })
  const handleDelete = (id: number) => deleteSO.mutate(id)

  return {
    // State
    timeFilter: filterState.timeFilter,
    setTimeFilter,
    searchQuery: filterState.searchQuery,
    setSearchQuery,
    currentPage: filterState.currentPage,
    setCurrentPage,
    pageSize: filterState.pageSize,
    
    // Data
    orders,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createSO,
    deleteSO,
    confirmSO,
    fulfillSO,
    shipSO,
    deliverSO,
    cancelSO,
    
    // Handlers
    handleSearch,
    handleConfirm,
    handleFulfill,
    handleShip,
    handleDeliver,
    handleCancel,
    handleDelete,
  }
}
