import { useState, useReducer } from "react"
import { useDepartments as useDepartmentsQuery, useCreateDepartment, useUpdateDepartment, useDeleteDepartment } from "@/services/department"
import type { Department } from "@/types"
import type { TimeFilterValue } from "@/components/ui/TimeFilterBar"
import { type DepartmentFormData } from "@/lib/schemas"

interface DepartmentStats {
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
  selectedDepartment: Department | null
}

type ModalAction =
  | { type: "OPEN_ADD" }
  | { type: "CLOSE_ADD" }
  | { type: "OPEN_EDIT"; payload: Department }
  | { type: "CLOSE_EDIT" }
  | { type: "OPEN_DELETE"; payload: Department }
  | { type: "CLOSE_DELETE" }

function modalReducer(state: ModalState, action: ModalAction): ModalState {
  switch (action.type) {
    case "OPEN_ADD":
      return { ...state, isAddModalOpen: true }
    case "CLOSE_ADD":
      return { ...state, isAddModalOpen: false }
    case "OPEN_EDIT":
      return { ...state, isEditModalOpen: true, selectedDepartment: action.payload }
    case "CLOSE_EDIT":
      return { ...state, isEditModalOpen: false, selectedDepartment: null }
    case "OPEN_DELETE":
      return { ...state, isDeleteModalOpen: true, selectedDepartment: action.payload }
    case "CLOSE_DELETE":
      return { ...state, isDeleteModalOpen: false, selectedDepartment: null }
    default:
      return state
  }
}

interface UseDepartmentsReturn {
  // State
  timeFilter: TimeFilterValue
  setTimeFilter: (value: TimeFilterValue) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  stats: DepartmentStats
  
  // Modal states
  isAddModalOpen: boolean
  setIsAddModalOpen: (value: boolean) => void
  isEditModalOpen: boolean
  setIsEditModalOpen: (value: boolean) => void
  isDeleteModalOpen: boolean
  setIsDeleteModalOpen: (value: boolean) => void
  selectedDepartment: Department | null
  setSelectedDepartment: (department: Department | null) => void
  
  // Data
  departments: Department[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createDepartment: ReturnType<typeof useCreateDepartment>
  updateDepartment: ReturnType<typeof useUpdateDepartment>
  deleteDepartment: ReturnType<typeof useDeleteDepartment>
  
  // Handlers
  handleSearch: () => void
  handleAddDepartment: (data: DepartmentFormData) => void
  handleEditDepartment: (data: DepartmentFormData) => void
  handleDeleteDepartment: () => void
  openEditModal: (department: Department) => void
  openDeleteModal: (department: Department) => void
  closeAddModal: () => void
  closeEditModal: () => void
  closeDeleteModal: () => void
}

export function useDepartments(): UseDepartmentsReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })

  const [stats] = useState<DepartmentStats>({
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
    selectedDepartment: null,
  })
  
  // React Query hooks
  const { data: departmentsData, isLoading } = useDepartmentsQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createDepartment = useCreateDepartment()
  const updateDepartment = useUpdateDepartment()
  const deleteDepartment = useDeleteDepartment()
  
  const departments = departmentsData?.data.content || []
  const totalElements = departmentsData?.data.totalElements || 0
  const totalPages = departmentsData?.data.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilterValue) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Modal actions
  const setIsAddModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_ADD" }) : dispatchModal({ type: "CLOSE_ADD" })
  const setIsEditModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_EDIT", payload: modalState.selectedDepartment! }) : dispatchModal({ type: "CLOSE_EDIT" })
  const setIsDeleteModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_DELETE", payload: modalState.selectedDepartment! }) : dispatchModal({ type: "CLOSE_DELETE" })
  const setSelectedDepartment = (department: Department | null) => {
    if (department && modalState.isEditModalOpen) {
      dispatchModal({ type: "OPEN_EDIT", payload: department })
    } else if (department && modalState.isDeleteModalOpen) {
      dispatchModal({ type: "OPEN_DELETE", payload: department })
    }
  }
  const closeAddModal = () => dispatchModal({ type: "CLOSE_ADD" })
  const closeEditModal = () => dispatchModal({ type: "CLOSE_EDIT" })
  const closeDeleteModal = () => dispatchModal({ type: "CLOSE_DELETE" })
  
  // Mutation handlers - callbacks handle toasts
  const handleAddDepartment = (data: DepartmentFormData) => {
    createDepartment.mutate(data)
    closeAddModal()
  }
  
  const handleEditDepartment = (data: DepartmentFormData) => {
    if (!modalState.selectedDepartment) return
    updateDepartment.mutate({ id: modalState.selectedDepartment.id, data })
    closeEditModal()
  }
  
  const handleDeleteDepartment = () => {
    if (!modalState.selectedDepartment) return
    deleteDepartment.mutate(modalState.selectedDepartment.id)
    closeDeleteModal()
  }
  
  const openEditModal = (department: Department) => dispatchModal({ type: "OPEN_EDIT", payload: department })
  const openDeleteModal = (department: Department) => dispatchModal({ type: "OPEN_DELETE", payload: department })

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
    selectedDepartment: modalState.selectedDepartment,
    setSelectedDepartment,
    
    // Data
    departments,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createDepartment,
    updateDepartment,
    deleteDepartment,
    
    // Handlers
    handleSearch,
    handleAddDepartment,
    handleEditDepartment,
    handleDeleteDepartment,
    openEditModal,
    openDeleteModal,
    closeAddModal,
    closeEditModal,
    closeDeleteModal,
  }
}
