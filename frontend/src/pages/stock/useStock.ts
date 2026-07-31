import { useReducer } from "react"
import {
  useInventory,
  useCreateInventoryItem,
  useUpdateInventoryItem,
} from "@/services/stock"
import type { InventoryItem } from "@/types"

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
  isAddModalOpen: boolean
  isEditModalOpen: boolean
  selectedStock: InventoryItem | null
}

type ModalAction =
  | { type: "OPEN_ADD" }
  | { type: "CLOSE_ADD" }
  | { type: "OPEN_EDIT"; payload: InventoryItem }
  | { type: "CLOSE_EDIT" }

function modalReducer(state: ModalState, action: ModalAction): ModalState {
  switch (action.type) {
    case "OPEN_ADD":
      return { ...state, isAddModalOpen: true }
    case "CLOSE_ADD":
      return { ...state, isAddModalOpen: false }
    case "OPEN_EDIT":
      return { ...state, isEditModalOpen: true, selectedStock: action.payload }
    case "CLOSE_EDIT":
      return { ...state, isEditModalOpen: false, selectedStock: null }
    default:
      return state
  }
}

interface UseStockReturn {
  // State
  timeFilter: TimeFilter
  setTimeFilter: (value: TimeFilter) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  
  // Modal states
  isAddModalOpen: boolean
  setIsAddModalOpen: (value: boolean) => void
  isEditModalOpen: boolean
  setIsEditModalOpen: (value: boolean) => void
  selectedStock: InventoryItem | null
  setSelectedStock: (stock: InventoryItem | null) => void
  
  // Data
  stockItems: InventoryItem[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createInventory: ReturnType<typeof useCreateInventoryItem>
  updateInventory: ReturnType<typeof useUpdateInventoryItem>
  
  // Loading states for modals
  isAdding: boolean
  isEditing: boolean
  
  // Handlers
  handleSearch: () => void
  handleAddStock: (data: Partial<InventoryItem>) => void
  handleEditStock: (data: Partial<InventoryItem>) => void
  openEditModal: (stock: InventoryItem) => void
  closeAddModal: () => void
  closeEditModal: () => void
}

export function useStock(): UseStockReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })
  
  // Consolidated modal state
  const [modalState, dispatchModal] = useReducer(modalReducer, {
    isAddModalOpen: false,
    isEditModalOpen: false,
    selectedStock: null,
  })
  
  // React Query hooks
  const { data: stockData, isLoading } = useInventory({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createInventory = useCreateInventoryItem()
  const updateInventory = useUpdateInventoryItem()
  
  const stockItems = stockData?.data.content || []
  const totalElements = stockData?.data.totalElements || 0
  const totalPages = stockData?.data.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilter) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Modal actions
  const setIsAddModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_ADD" }) : dispatchModal({ type: "CLOSE_ADD" })
  const setIsEditModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_EDIT", payload: modalState.selectedStock! }) : dispatchModal({ type: "CLOSE_EDIT" })
  const setSelectedStock = (stock: InventoryItem | null) => {
    if (stock && modalState.isEditModalOpen) {
      dispatchModal({ type: "OPEN_EDIT", payload: stock })
    }
  }
  const closeAddModal = () => dispatchModal({ type: "CLOSE_ADD" })
  const closeEditModal = () => dispatchModal({ type: "CLOSE_EDIT" })
  
  // Mutation handlers - callbacks handle toasts
  const handleAddStock = (data: Partial<InventoryItem>) => {
    createInventory.mutate(data)
    closeAddModal()
  }
  
  const handleEditStock = (data: Partial<InventoryItem>) => {
    if (!modalState.selectedStock) return
    updateInventory.mutate({ id: modalState.selectedStock.id, data })
    closeEditModal()
  }
  
  const openEditModal = (stock: InventoryItem) => dispatchModal({ type: "OPEN_EDIT", payload: stock })
  
  return {
    // State
    timeFilter: filterState.timeFilter,
    setTimeFilter,
    searchQuery: filterState.searchQuery,
    setSearchQuery,
    currentPage: filterState.currentPage,
    setCurrentPage,
    pageSize: filterState.pageSize,
    
    // Modal states
    isAddModalOpen: modalState.isAddModalOpen,
    setIsAddModalOpen,
    isEditModalOpen: modalState.isEditModalOpen,
    setIsEditModalOpen,
    selectedStock: modalState.selectedStock,
    setSelectedStock,
    
    // Data
    stockItems,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createInventory,
    updateInventory,
    
    // Loading states for modals
    isAdding: createInventory.isPending,
    isEditing: updateInventory.isPending,
    
    // Handlers
    handleSearch,
    handleAddStock,
    handleEditStock,
    openEditModal,
    closeAddModal,
    closeEditModal,
  }
}
