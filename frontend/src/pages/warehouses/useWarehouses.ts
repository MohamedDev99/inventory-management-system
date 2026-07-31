import { useState, useReducer } from "react"
import { useWarehouses as useWarehousesQuery, useCreateWarehouse, useUpdateWarehouse, useDeleteWarehouse } from "@/services/warehouse"
import type { Warehouse } from "@/types"
import type { WarehouseFormData } from "@/lib/schemas"
import type { TimeFilterValue } from "@/components/ui/TimeFilterBar"

interface WarehouseStats {
  active: number
  inactive: number
  deleted: number
  activeTrend: number
  inactiveTrend: number
  deletedTrend: number
}

// Consolidate filter state
interface FilterState {
  timeFilter: TimeFilterValue
  searchQuery: string
  currentPage: number
  pageSize: number
}

type FilterAction =
  | { type: "SET_TIME_FILTER"; payload: TimeFilterValue }
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
  isDeleteModalOpen: boolean
  selectedWarehouse: Warehouse | null
}

type ModalAction =
  | { type: "OPEN_ADD" }
  | { type: "CLOSE_ADD" }
  | { type: "OPEN_EDIT"; payload: Warehouse }
  | { type: "CLOSE_EDIT" }
  | { type: "OPEN_DELETE"; payload: Warehouse }
  | { type: "CLOSE_DELETE" }

function modalReducer(state: ModalState, action: ModalAction): ModalState {
  switch (action.type) {
    case "OPEN_ADD":
      return { ...state, isAddModalOpen: true }
    case "CLOSE_ADD":
      return { ...state, isAddModalOpen: false }
    case "OPEN_EDIT":
      return { ...state, isEditModalOpen: true, selectedWarehouse: action.payload }
    case "CLOSE_EDIT":
      return { ...state, isEditModalOpen: false, selectedWarehouse: null }
    case "OPEN_DELETE":
      return { ...state, isDeleteModalOpen: true, selectedWarehouse: action.payload }
    case "CLOSE_DELETE":
      return { ...state, isDeleteModalOpen: false, selectedWarehouse: null }
    default:
      return state
  }
}

interface UseWarehousesReturn {
  // State
  timeFilter: TimeFilterValue
  setTimeFilter: (value: TimeFilterValue) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  stats: WarehouseStats
  
  // Modal states
  isAddModalOpen: boolean
  setIsAddModalOpen: (value: boolean) => void
  isEditModalOpen: boolean
  setIsEditModalOpen: (value: boolean) => void
  isDeleteModalOpen: boolean
  setIsDeleteModalOpen: (value: boolean) => void
  selectedWarehouse: Warehouse | null
  setSelectedWarehouse: (warehouse: Warehouse | null) => void
  
  // Data
  warehouses: Warehouse[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createWarehouse: ReturnType<typeof useCreateWarehouse>
  updateWarehouse: ReturnType<typeof useUpdateWarehouse>
  deleteWarehouse: ReturnType<typeof useDeleteWarehouse>
  
  // Handlers
  handleSearch: () => void
  handleAddWarehouse: (data: WarehouseFormData) => void
  handleEditWarehouse: (data: WarehouseFormData) => void
  handleDeleteWarehouse: () => void
  openEditModal: (warehouse: Warehouse) => void
  openDeleteModal: (warehouse: Warehouse) => void
  closeAddModal: () => void
  closeEditModal: () => void
  closeDeleteModal: () => void
}

export function useWarehouses(): UseWarehousesReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })

  const [stats] = useState<WarehouseStats>({
    active: 100,
    inactive: 19,
    deleted: 10,
    activeTrend: 12,
    inactiveTrend: 12,
    deletedTrend: 12,
  })
  
  // Consolidated modal state
  const [modalState, dispatchModal] = useReducer(modalReducer, {
    isAddModalOpen: false,
    isEditModalOpen: false,
    isDeleteModalOpen: false,
    selectedWarehouse: null,
  })
  
  // React Query hooks
  const { data: warehousesData, isLoading } = useWarehousesQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createWarehouse = useCreateWarehouse()
  const updateWarehouse = useUpdateWarehouse()
  const deleteWarehouse = useDeleteWarehouse()
  
  const warehouses = warehousesData?.data?.content || []
  const totalElements = warehousesData?.data?.totalElements || 0
  const totalPages = warehousesData?.data?.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilterValue) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Modal actions
  const setIsAddModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_ADD" }) : dispatchModal({ type: "CLOSE_ADD" })
  const setIsEditModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_EDIT", payload: modalState.selectedWarehouse! }) : dispatchModal({ type: "CLOSE_EDIT" })
  const setIsDeleteModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_DELETE", payload: modalState.selectedWarehouse! }) : dispatchModal({ type: "CLOSE_DELETE" })
  const setSelectedWarehouse = (warehouse: Warehouse | null) => {
    if (warehouse && modalState.isEditModalOpen) {
      dispatchModal({ type: "OPEN_EDIT", payload: warehouse })
    } else if (warehouse && modalState.isDeleteModalOpen) {
      dispatchModal({ type: "OPEN_DELETE", payload: warehouse })
    }
  }
  const closeAddModal = () => dispatchModal({ type: "CLOSE_ADD" })
  const closeEditModal = () => dispatchModal({ type: "CLOSE_EDIT" })
  const closeDeleteModal = () => dispatchModal({ type: "CLOSE_DELETE" })
  
  // Mutation handlers - callbacks handle toasts
  const handleAddWarehouse = (data: WarehouseFormData) => {
    createWarehouse.mutate(data)
    closeAddModal()
  }
  
  const handleEditWarehouse = (data: WarehouseFormData) => {
    if (!modalState.selectedWarehouse) return
    updateWarehouse.mutate({ id: modalState.selectedWarehouse.id, data })
    closeEditModal()
  }
  
  const handleDeleteWarehouse = () => {
    if (!modalState.selectedWarehouse) return
    deleteWarehouse.mutate(modalState.selectedWarehouse.id)
    closeDeleteModal()
  }
  
  const openEditModal = (warehouse: Warehouse) => dispatchModal({ type: "OPEN_EDIT", payload: warehouse })
  const openDeleteModal = (warehouse: Warehouse) => dispatchModal({ type: "OPEN_DELETE", payload: warehouse })

  return {
    // State
    timeFilter: filterState.timeFilter,
    setTimeFilter,
    searchQuery: filterState.searchQuery,
    setSearchQuery,
    currentPage: filterState.currentPage,
    setCurrentPage,
    pageSize: filterState.pageSize,
    stats,
    
    // Modal states
    isAddModalOpen: modalState.isAddModalOpen,
    setIsAddModalOpen,
    isEditModalOpen: modalState.isEditModalOpen,
    setIsEditModalOpen,
    isDeleteModalOpen: modalState.isDeleteModalOpen,
    setIsDeleteModalOpen,
    selectedWarehouse: modalState.selectedWarehouse,
    setSelectedWarehouse,
    
    // Data
    warehouses,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createWarehouse,
    updateWarehouse,
    deleteWarehouse,
    
    // Handlers
    handleSearch,
    handleAddWarehouse,
    handleEditWarehouse,
    handleDeleteWarehouse,
    openEditModal,
    openDeleteModal,
    closeAddModal,
    closeEditModal,
    closeDeleteModal,
  }
}
