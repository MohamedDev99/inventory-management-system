import { useState, useReducer } from "react"
import { useEmployees as useEmployeesQuery, useCreateEmployee, useUpdateEmployee, useDeleteEmployee } from "@/services/employee"
import type { Employee, EmployeeFormData } from "@/types"
import type { TimeFilterValue } from "@/components/ui/TimeFilterBar"

interface EmployeeStats {
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
  selectedEmployee: Employee | null
}

type ModalAction =
  | { type: "OPEN_ADD" }
  | { type: "CLOSE_ADD" }
  | { type: "OPEN_EDIT"; payload: Employee }
  | { type: "CLOSE_EDIT" }
  | { type: "OPEN_DELETE"; payload: Employee }
  | { type: "CLOSE_DELETE" }

function modalReducer(state: ModalState, action: ModalAction): ModalState {
  switch (action.type) {
    case "OPEN_ADD":
      return { ...state, isAddModalOpen: true }
    case "CLOSE_ADD":
      return { ...state, isAddModalOpen: false }
    case "OPEN_EDIT":
      return { ...state, isEditModalOpen: true, selectedEmployee: action.payload }
    case "CLOSE_EDIT":
      return { ...state, isEditModalOpen: false, selectedEmployee: null }
    case "OPEN_DELETE":
      return { ...state, isDeleteModalOpen: true, selectedEmployee: action.payload }
    case "CLOSE_DELETE":
      return { ...state, isDeleteModalOpen: false, selectedEmployee: null }
    default:
      return state
  }
}

interface UseEmployeesReturn {
  // State
  timeFilter: TimeFilterValue
  setTimeFilter: (value: TimeFilterValue) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  stats: EmployeeStats
  
  // Modal states
  isAddModalOpen: boolean
  setIsAddModalOpen: (value: boolean) => void
  isEditModalOpen: boolean
  setIsEditModalOpen: (value: boolean) => void
  isDeleteModalOpen: boolean
  setIsDeleteModalOpen: (value: boolean) => void
  selectedEmployee: Employee | null
  setSelectedEmployee: (employee: Employee | null) => void
  
  // Data
  employees: Employee[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createEmployee: ReturnType<typeof useCreateEmployee>
  updateEmployee: ReturnType<typeof useUpdateEmployee>
  deleteEmployee: ReturnType<typeof useDeleteEmployee>
  
  // Handlers
  handleSearch: () => void
  handleAddEmployee: (data: EmployeeFormData) => void
  handleEditEmployee: (data: EmployeeFormData) => void
  handleDeleteEmployee: () => void
  openEditModal: (employee: Employee) => void
  openDeleteModal: (employee: Employee) => void
  closeAddModal: () => void
  closeEditModal: () => void
  closeDeleteModal: () => void
}

export function useEmployees(): UseEmployeesReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })

  const [stats] = useState<EmployeeStats>({
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
    selectedEmployee: null,
  })
  
  // React Query hooks
  const { data: employeesData, isLoading } = useEmployeesQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createEmployee = useCreateEmployee()
  const updateEmployee = useUpdateEmployee()
  const deleteEmployee = useDeleteEmployee()
  
  const employees = employeesData?.data.content || []
  const totalElements = employeesData?.data.totalElements || 0
  const totalPages = employeesData?.data.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilterValue) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Modal actions
  const setIsAddModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_ADD" }) : dispatchModal({ type: "CLOSE_ADD" })
  const setIsEditModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_EDIT", payload: modalState.selectedEmployee! }) : dispatchModal({ type: "CLOSE_EDIT" })
  const setIsDeleteModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_DELETE", payload: modalState.selectedEmployee! }) : dispatchModal({ type: "CLOSE_DELETE" })
  const setSelectedEmployee = (employee: Employee | null) => {
    if (employee && modalState.isEditModalOpen) {
      dispatchModal({ type: "OPEN_EDIT", payload: employee })
    } else if (employee && modalState.isDeleteModalOpen) {
      dispatchModal({ type: "OPEN_DELETE", payload: employee })
    }
  }
  const closeAddModal = () => dispatchModal({ type: "CLOSE_ADD" })
  const closeEditModal = () => dispatchModal({ type: "CLOSE_EDIT" })
  const closeDeleteModal = () => dispatchModal({ type: "CLOSE_DELETE" })
  
  // Mutation handlers - callbacks handle toasts
  const handleAddEmployee = (data: EmployeeFormData) => {
    createEmployee.mutate(data)
    closeAddModal()
  }
  
  const handleEditEmployee = (data: EmployeeFormData) => {
    if (!modalState.selectedEmployee) return
    updateEmployee.mutate({ id: modalState.selectedEmployee.id, data })
    closeEditModal()
  }
  
  const handleDeleteEmployee = () => {
    if (!modalState.selectedEmployee) return
    deleteEmployee.mutate(modalState.selectedEmployee.id)
    closeDeleteModal()
  }
  
  const openEditModal = (employee: Employee) => dispatchModal({ type: "OPEN_EDIT", payload: employee })
  const openDeleteModal = (employee: Employee) => dispatchModal({ type: "OPEN_DELETE", payload: employee })

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
    selectedEmployee: modalState.selectedEmployee,
    setSelectedEmployee,
    
    // Data
    employees,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createEmployee,
    updateEmployee,
    deleteEmployee,
    
    // Handlers
    handleSearch,
    handleAddEmployee,
    handleEditEmployee,
    handleDeleteEmployee,
    openEditModal,
    openDeleteModal,
    closeAddModal,
    closeEditModal,
    closeDeleteModal,
  }
}
