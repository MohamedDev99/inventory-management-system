import { useState, useReducer } from "react"
import { useSuppliers as useSuppliersQuery, useCreateSupplier, useUpdateSupplier, useDeleteSupplier } from "@/services/supplier"
import type { Supplier } from "@/types"
import type { CreateSupplierRequest, UpdateSupplierRequest } from "@/lib/schemas/supplier/request"
import type { TimeFilterValue } from "@/components/ui/TimeFilterBar"

interface SupplierStats {
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
  selectedSupplier: Supplier | null
}

type ModalAction =
  | { type: "OPEN_ADD" }
  | { type: "CLOSE_ADD" }
  | { type: "OPEN_EDIT"; payload: Supplier }
  | { type: "CLOSE_EDIT" }
  | { type: "OPEN_DELETE"; payload: Supplier }
  | { type: "CLOSE_DELETE" }

function modalReducer(state: ModalState, action: ModalAction): ModalState {
  switch (action.type) {
    case "OPEN_ADD":
      return { ...state, isAddModalOpen: true }
    case "CLOSE_ADD":
      return { ...state, isAddModalOpen: false }
    case "OPEN_EDIT":
      return { ...state, isEditModalOpen: true, selectedSupplier: action.payload }
    case "CLOSE_EDIT":
      return { ...state, isEditModalOpen: false, selectedSupplier: null }
    case "OPEN_DELETE":
      return { ...state, isDeleteModalOpen: true, selectedSupplier: action.payload }
    case "CLOSE_DELETE":
      return { ...state, isDeleteModalOpen: false, selectedSupplier: null }
    default:
      return state
  }
}

interface UseSuppliersReturn {
  // State
  timeFilter: TimeFilterValue
  setTimeFilter: (value: TimeFilterValue) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  stats: SupplierStats
  
  // Modal states
  isAddModalOpen: boolean
  setIsAddModalOpen: (value: boolean) => void
  isEditModalOpen: boolean
  setIsEditModalOpen: (value: boolean) => void
  isDeleteModalOpen: boolean
  setIsDeleteModalOpen: (value: boolean) => void
  selectedSupplier: Supplier | null
  setSelectedSupplier: (supplier: Supplier | null) => void
  
  // Data
  suppliers: Supplier[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createSupplier: ReturnType<typeof useCreateSupplier>
  updateSupplier: ReturnType<typeof useUpdateSupplier>
  deleteSupplier: ReturnType<typeof useDeleteSupplier>
  
  // Handlers
  handleSearch: () => void
  // NOTE: Using CreateSupplierRequest for add, UpdateSupplierRequest for edit
  // These match the API schema types
  handleAddSupplier: (data: CreateSupplierRequest) => void
  handleEditSupplier: (data: UpdateSupplierRequest) => void
  handleDeleteSupplier: () => void
  openEditModal: (supplier: Supplier) => void
  openDeleteModal: (supplier: Supplier) => void
  closeAddModal: () => void
  closeEditModal: () => void
  closeDeleteModal: () => void
}

export function useSuppliers(): UseSuppliersReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })

  const [stats] = useState<SupplierStats>({
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
    selectedSupplier: null,
  })
  
  // React Query hooks
  const { data: suppliersData, isLoading } = useSuppliersQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createSupplier = useCreateSupplier()
  const updateSupplier = useUpdateSupplier()
  const deleteSupplier = useDeleteSupplier()
  
  const suppliers = suppliersData?.data.content || []
  const totalElements = suppliersData?.data.totalElements || 0
  const totalPages = suppliersData?.data.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilterValue) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Modal actions
  const setIsAddModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_ADD" }) : dispatchModal({ type: "CLOSE_ADD" })
  const setIsEditModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_EDIT", payload: modalState.selectedSupplier! }) : dispatchModal({ type: "CLOSE_EDIT" })
  const setIsDeleteModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_DELETE", payload: modalState.selectedSupplier! }) : dispatchModal({ type: "CLOSE_DELETE" })
  const setSelectedSupplier = (supplier: Supplier | null) => {
    if (supplier && modalState.isEditModalOpen) {
      dispatchModal({ type: "OPEN_EDIT", payload: supplier })
    } else if (supplier && modalState.isDeleteModalOpen) {
      dispatchModal({ type: "OPEN_DELETE", payload: supplier })
    }
  }
  const closeAddModal = () => dispatchModal({ type: "CLOSE_ADD" })
  const closeEditModal = () => dispatchModal({ type: "CLOSE_EDIT" })
  const closeDeleteModal = () => dispatchModal({ type: "CLOSE_DELETE" })
  
  // Mutation handlers - callbacks handle toasts
  const handleAddSupplier = (data: CreateSupplierRequest) => {
    createSupplier.mutate(data)
    closeAddModal()
  }
  
  const handleEditSupplier = (data: UpdateSupplierRequest) => {
    if (!modalState.selectedSupplier) return
    updateSupplier.mutate({ id: modalState.selectedSupplier.id, data })
    closeEditModal()
  }
  
  const handleDeleteSupplier = () => {
    if (!modalState.selectedSupplier) return
    deleteSupplier.mutate(modalState.selectedSupplier.id)
    closeDeleteModal()
  }
  
  const openEditModal = (supplier: Supplier) => dispatchModal({ type: "OPEN_EDIT", payload: supplier })
  const openDeleteModal = (supplier: Supplier) => dispatchModal({ type: "OPEN_DELETE", payload: supplier })

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
    selectedSupplier: modalState.selectedSupplier,
    setSelectedSupplier,
    
    // Data
    suppliers,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createSupplier,
    updateSupplier,
    deleteSupplier,
    
    // Handlers
    handleSearch,
    handleAddSupplier,
    handleEditSupplier,
    handleDeleteSupplier,
    openEditModal,
    openDeleteModal,
    closeAddModal,
    closeEditModal,
    closeDeleteModal,
  }
}
