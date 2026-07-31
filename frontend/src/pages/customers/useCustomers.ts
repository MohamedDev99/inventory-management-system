import { useState, useReducer } from "react"
import { useCustomers as useCustomersQuery, useCreateCustomer, useUpdateCustomer, useDeleteCustomer } from "@/services/customer"
import type { Customer } from "@/types"
import type { CreateCustomerRequest, UpdateCustomerRequest } from "@/lib/schemas/customer/request"
import type { TimeFilterValue } from "@/components/ui/TimeFilterBar"

interface CustomerStats {
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
  selectedCustomer: Customer | null
}

type ModalAction =
  | { type: "OPEN_ADD" }
  | { type: "CLOSE_ADD" }
  | { type: "OPEN_EDIT"; payload: Customer }
  | { type: "CLOSE_EDIT" }
  | { type: "OPEN_DELETE"; payload: Customer }
  | { type: "CLOSE_DELETE" }

function modalReducer(state: ModalState, action: ModalAction): ModalState {
  switch (action.type) {
    case "OPEN_ADD":
      return { ...state, isAddModalOpen: true }
    case "CLOSE_ADD":
      return { ...state, isAddModalOpen: false }
    case "OPEN_EDIT":
      return { ...state, isEditModalOpen: true, selectedCustomer: action.payload }
    case "CLOSE_EDIT":
      return { ...state, isEditModalOpen: false, selectedCustomer: null }
    case "OPEN_DELETE":
      return { ...state, isDeleteModalOpen: true, selectedCustomer: action.payload }
    case "CLOSE_DELETE":
      return { ...state, isDeleteModalOpen: false, selectedCustomer: null }
    default:
      return state
  }
}

interface UseCustomersReturn {
  // State
  timeFilter: TimeFilterValue
  setTimeFilter: (value: TimeFilterValue) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  stats: CustomerStats
  
  // Modal states
  isAddModalOpen: boolean
  setIsAddModalOpen: (value: boolean) => void
  isEditModalOpen: boolean
  setIsEditModalOpen: (value: boolean) => void
  isDeleteModalOpen: boolean
  setIsDeleteModalOpen: (value: boolean) => void
  selectedCustomer: Customer | null
  setSelectedCustomer: (customer: Customer | null) => void
  
  // Data
  customers: Customer[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createCustomer: ReturnType<typeof useCreateCustomer>
  updateCustomer: ReturnType<typeof useUpdateCustomer>
  deleteCustomer: ReturnType<typeof useDeleteCustomer>
  
  // Handlers
  handleSearch: () => void
  handleAddCustomer: (data: CreateCustomerRequest) => void
  handleEditCustomer: (data: UpdateCustomerRequest) => void
  handleDeleteCustomer: () => void
  openEditModal: (customer: Customer) => void
  openDeleteModal: (customer: Customer) => void
  closeAddModal: () => void
  closeEditModal: () => void
  closeDeleteModal: () => void
}

export function useCustomers(): UseCustomersReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })

  const [stats] = useState<CustomerStats>({
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
    selectedCustomer: null,
  })
  
  // React Query hooks
  const { data: customersData, isLoading } = useCustomersQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createCustomer = useCreateCustomer()
  const updateCustomer = useUpdateCustomer()
  const deleteCustomer = useDeleteCustomer()
  
  const customers = customersData?.data?.content || []
  const totalElements = customersData?.data?.totalElements || 0
  const totalPages = customersData?.data?.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilterValue) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Modal actions
  const setIsAddModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_ADD" }) : dispatchModal({ type: "CLOSE_ADD" })
  const setIsEditModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_EDIT", payload: modalState.selectedCustomer! }) : dispatchModal({ type: "CLOSE_EDIT" })
  const setIsDeleteModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_DELETE", payload: modalState.selectedCustomer! }) : dispatchModal({ type: "CLOSE_DELETE" })
  const setSelectedCustomer = (customer: Customer | null) => {
    if (customer && modalState.isEditModalOpen) {
      dispatchModal({ type: "OPEN_EDIT", payload: customer })
    } else if (customer && modalState.isDeleteModalOpen) {
      dispatchModal({ type: "OPEN_DELETE", payload: customer })
    }
  }
  const closeAddModal = () => dispatchModal({ type: "CLOSE_ADD" })
  const closeEditModal = () => dispatchModal({ type: "CLOSE_EDIT" })
  const closeDeleteModal = () => dispatchModal({ type: "CLOSE_DELETE" })
  
  // Mutation handlers - callbacks handle toasts
  const handleAddCustomer = (data: CreateCustomerRequest) => {
    createCustomer.mutate({ ...data })
    closeAddModal()
  }
  
  const handleEditCustomer = (data: UpdateCustomerRequest) => {
    if (!modalState.selectedCustomer) return
    updateCustomer.mutate({ id: modalState.selectedCustomer.id, data })
    closeEditModal()
  }
  
  const handleDeleteCustomer = () => {
    if (!modalState.selectedCustomer) return
    deleteCustomer.mutate(modalState.selectedCustomer.id)
    closeDeleteModal()
  }
  
  const openEditModal = (customer: Customer) => dispatchModal({ type: "OPEN_EDIT", payload: customer })
  const openDeleteModal = (customer: Customer) => dispatchModal({ type: "OPEN_DELETE", payload: customer })

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
    selectedCustomer: modalState.selectedCustomer,
    setSelectedCustomer,
    
    // Data
    customers,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createCustomer,
    updateCustomer,
    deleteCustomer,
    
    // Handlers
    handleSearch,
    handleAddCustomer,
    handleEditCustomer,
    handleDeleteCustomer,
    openEditModal,
    openDeleteModal,
    closeAddModal,
    closeEditModal,
    closeDeleteModal,
  }
}
