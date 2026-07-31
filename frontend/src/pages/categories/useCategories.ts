import { useState, useReducer } from "react"
import { useCategories as useCategoriesQuery, useCreateCategory, useUpdateCategory, useDeleteCategory } from "@/services/category"
import type { Category } from "@/types"
import type { TimeFilterValue } from "@/components/ui/TimeFilterBar"
import type { CategoryFormData } from "@/lib/schemas"

interface CategoryStats {
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
  selectedCategory: Category | null
}

type ModalAction =
  | { type: "OPEN_ADD" }
  | { type: "CLOSE_ADD" }
  | { type: "OPEN_EDIT"; payload: Category }
  | { type: "CLOSE_EDIT" }
  | { type: "OPEN_DELETE"; payload: Category }
  | { type: "CLOSE_DELETE" }

function modalReducer(state: ModalState, action: ModalAction): ModalState {
  switch (action.type) {
    case "OPEN_ADD":
      return { ...state, isAddModalOpen: true }
    case "CLOSE_ADD":
      return { ...state, isAddModalOpen: false }
    case "OPEN_EDIT":
      return { ...state, isEditModalOpen: true, selectedCategory: action.payload }
    case "CLOSE_EDIT":
      return { ...state, isEditModalOpen: false, selectedCategory: null }
    case "OPEN_DELETE":
      return { ...state, isDeleteModalOpen: true, selectedCategory: action.payload }
    case "CLOSE_DELETE":
      return { ...state, isDeleteModalOpen: false, selectedCategory: null }
    default:
      return state
  }
}

interface UseCategoriesReturn {
  // State
  timeFilter: TimeFilterValue
  setTimeFilter: (value: TimeFilterValue) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  stats: CategoryStats
  
  // Modal states
  isAddModalOpen: boolean
  setIsAddModalOpen: (value: boolean) => void
  isEditModalOpen: boolean
  setIsEditModalOpen: (value: boolean) => void
  isDeleteModalOpen: boolean
  setIsDeleteModalOpen: (value: boolean) => void
  selectedCategory: Category | null
  setSelectedCategory: (category: Category | null) => void
  
  // Data
  categories: Category[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createCategory: ReturnType<typeof useCreateCategory>
  updateCategory: ReturnType<typeof useUpdateCategory>
  deleteCategory: ReturnType<typeof useDeleteCategory>
  
  // Handlers
  handleSearch: () => void
  handleAddCategory: (data: CategoryFormData) => void
  handleEditCategory: (data: CategoryFormData) => void
  handleDeleteCategory: () => void
  openEditModal: (category: Category) => void
  openDeleteModal: (category: Category) => void
  closeAddModal: () => void
  closeEditModal: () => void
  closeDeleteModal: () => void
}

export function useCategories(): UseCategoriesReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
  })

  const [stats] = useState<CategoryStats>({
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
    selectedCategory: null,
  })
  
  // React Query hooks
  const { data: categoriesData, isLoading } = useCategoriesQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery,
  })
  
  const createCategory = useCreateCategory()
  const updateCategory = useUpdateCategory()
  const deleteCategory = useDeleteCategory()
  
  const categories = categoriesData?.data.content || []
  const totalElements = categoriesData?.data.totalElements || 0
  const totalPages = categoriesData?.data.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilterValue) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  
  // Modal actions
  const setIsAddModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_ADD" }) : dispatchModal({ type: "CLOSE_ADD" })
  const setIsEditModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_EDIT", payload: modalState.selectedCategory! }) : dispatchModal({ type: "CLOSE_EDIT" })
  const setIsDeleteModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_DELETE", payload: modalState.selectedCategory! }) : dispatchModal({ type: "CLOSE_DELETE" })
  const setSelectedCategory = (category: Category | null) => {
    if (category && modalState.isEditModalOpen) {
      dispatchModal({ type: "OPEN_EDIT", payload: category })
    } else if (category && modalState.isDeleteModalOpen) {
      dispatchModal({ type: "OPEN_DELETE", payload: category })
    }
  }
  const closeAddModal = () => dispatchModal({ type: "CLOSE_ADD" })
  const closeEditModal = () => dispatchModal({ type: "CLOSE_EDIT" })
  const closeDeleteModal = () => dispatchModal({ type: "CLOSE_DELETE" })
  
  // Mutation handlers - now just trigger mutations, callbacks handle toasts
  const handleAddCategory = (data: CategoryFormData) => {
    createCategory.mutate(data)
    closeAddModal()
  }
  
  const handleEditCategory = (data: CategoryFormData) => {
    if (!modalState.selectedCategory) return
    updateCategory.mutate({ id: modalState.selectedCategory.id, data })
    closeEditModal()
  }
  
  const handleDeleteCategory = () => {
    if (!modalState.selectedCategory) return
    deleteCategory.mutate(modalState.selectedCategory.id)
    closeDeleteModal()
  }
  
  const openEditModal = (category: Category) => dispatchModal({ type: "OPEN_EDIT", payload: category })
  const openDeleteModal = (category: Category) => dispatchModal({ type: "OPEN_DELETE", payload: category })

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
    selectedCategory: modalState.selectedCategory,
    setSelectedCategory,
    
    // Data
    categories,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createCategory,
    updateCategory,
    deleteCategory,
    
    // Handlers
    handleSearch,
    handleAddCategory,
    handleEditCategory,
    handleDeleteCategory,
    openEditModal,
    openDeleteModal,
    closeAddModal,
    closeEditModal,
    closeDeleteModal,
  }
}
