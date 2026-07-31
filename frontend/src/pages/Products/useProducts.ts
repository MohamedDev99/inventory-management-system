import {  useReducer } from "react"
import { useProducts as useProductsQuery, useCreateProduct, useUpdateProduct, useDeleteProduct } from "@/services/product"
import { useCategories } from "@/services/category"
import type { Product, ProductFormData, Category } from "@/types"
import type { TimeFilterValue } from "@/components/ui/TimeFilterBar"

type StockStatus = "ALL" | "IN_STOCK" | "LOW_STOCK" | "OUT_OF_STOCK"

// Consolidate filter state
interface FilterState {
  timeFilter: TimeFilterValue
  searchQuery: string
  currentPage: number
  pageSize: number
  selectedCategory: string
  selectedStockStatus: StockStatus
}

type FilterAction =
  | { type: "SET_TIME_FILTER"; payload: TimeFilterValue }
  | { type: "SET_SEARCH_QUERY"; payload: string }
  | { type: "SET_CURRENT_PAGE"; payload: number }
  | { type: "SET_SELECTED_CATEGORY"; payload: string }
  | { type: "SET_SELECTED_STOCK_STATUS"; payload: StockStatus }
  | { type: "RESET_PAGE" }
  | { type: "CLEAR_FILTERS" }

function filterReducer(state: FilterState, action: FilterAction): FilterState {
  switch (action.type) {
    case "SET_TIME_FILTER":
      return { ...state, timeFilter: action.payload }
    case "SET_SEARCH_QUERY":
      return { ...state, searchQuery: action.payload, currentPage: 0 }
    case "SET_CURRENT_PAGE":
      return { ...state, currentPage: action.payload }
    case "SET_SELECTED_CATEGORY":
      return { ...state, selectedCategory: action.payload, currentPage: 0 }
    case "SET_SELECTED_STOCK_STATUS":
      return { ...state, selectedStockStatus: action.payload, currentPage: 0 }
    case "RESET_PAGE":
      return { ...state, currentPage: 0 }
    case "CLEAR_FILTERS":
      return { ...state, selectedCategory: "ALL", selectedStockStatus: "ALL", currentPage: 0 }
    default:
      return state
  }
}

// Consolidate modal state
interface ModalState {
  isAddModalOpen: boolean
  isEditModalOpen: boolean
  isDeleteModalOpen: boolean
  selectedProduct: Product | null
}

type ModalAction =
  | { type: "OPEN_ADD" }
  | { type: "CLOSE_ADD" }
  | { type: "OPEN_EDIT"; payload: Product }
  | { type: "CLOSE_EDIT" }
  | { type: "OPEN_DELETE"; payload: Product }
  | { type: "CLOSE_DELETE" }

function modalReducer(state: ModalState, action: ModalAction): ModalState {
  switch (action.type) {
    case "OPEN_ADD":
      return { ...state, isAddModalOpen: true }
    case "CLOSE_ADD":
      return { ...state, isAddModalOpen: false }
    case "OPEN_EDIT":
      return { ...state, isEditModalOpen: true, selectedProduct: action.payload }
    case "CLOSE_EDIT":
      return { ...state, isEditModalOpen: false, selectedProduct: null }
    case "OPEN_DELETE":
      return { ...state, isDeleteModalOpen: true, selectedProduct: action.payload }
    case "CLOSE_DELETE":
      return { ...state, isDeleteModalOpen: false, selectedProduct: null }
    default:
      return state
  }
}

interface UseProductsReturn {
  // State
  timeFilter: TimeFilterValue
  setTimeFilter: (value: TimeFilterValue) => void
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  setCurrentPage: (page: number) => void
  pageSize: number
  
  // Filter states
  selectedCategory: string
  setSelectedCategory: (value: string) => void
  selectedStockStatus: StockStatus
  setSelectedStockStatus: (value: StockStatus) => void
  
  // Modal states
  isAddModalOpen: boolean
  setIsAddModalOpen: (value: boolean) => void
  isEditModalOpen: boolean
  setIsEditModalOpen: (value: boolean) => void
  isDeleteModalOpen: boolean
  setIsDeleteModalOpen: (value: boolean) => void
  selectedProduct: Product | null
  setSelectedProduct: (product: Product | null) => void
  
  // Data
  categories: Category[]
  products: Product[]
  totalElements: number
  totalPages: number
  isLoading: boolean
  
  // Mutations
  createProduct: ReturnType<typeof useCreateProduct>
  updateProduct: ReturnType<typeof useUpdateProduct>
  deleteProduct: ReturnType<typeof useDeleteProduct>
  
  // Handlers
  handleSearch: () => void
  handleAddProduct: (data: ProductFormData) => void
  handleEditProduct: (data: ProductFormData) => void
  handleDeleteProduct: () => void
  openEditModal: (product: Product) => void
  openDeleteModal: (product: Product) => void
  clearFilters: () => void
  hasActiveFilters: boolean
  closeAddModal: () => void
  closeEditModal: () => void
  closeDeleteModal: () => void
}

export function useProducts(): UseProductsReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    timeFilter: "1m",
    searchQuery: "",
    currentPage: 0,
    pageSize: 10,
    selectedCategory: "ALL",
    selectedStockStatus: "ALL",
  })
  
  // Consolidated modal state
  const [modalState, dispatchModal] = useReducer(modalReducer, {
    isAddModalOpen: false,
    isEditModalOpen: false,
    isDeleteModalOpen: false,
    selectedProduct: null,
  })
  
  // Fetch categories for filter
  const { data: categoriesData } = useCategories({ size: 100 })
  const categories = categoriesData?.data.content || []
  
  // React Query hooks
  const { data: productData, isLoading } = useProductsQuery({
    page: filterState.currentPage,
    size: filterState.pageSize,
    sort: "createdAt,desc",
    search: filterState.searchQuery || undefined,
    categoryId: filterState.selectedCategory !== "ALL" ? parseInt(filterState.selectedCategory) : undefined,
    stockStatus: filterState.selectedStockStatus !== "ALL" ? filterState.selectedStockStatus : undefined,
  })
  
  const createProduct = useCreateProduct()
  const updateProduct = useUpdateProduct()
  const deleteProduct = useDeleteProduct()
  
  const products = productData?.data.content || []
  const totalElements = productData?.data.totalElements || 0
  const totalPages = productData?.data.totalPages || 0
  
  // Filter actions
  const setTimeFilter = (value: TimeFilterValue) => dispatchFilter({ type: "SET_TIME_FILTER", payload: value })
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setCurrentPage = (page: number) => dispatchFilter({ type: "SET_CURRENT_PAGE", payload: page })
  const setSelectedCategory = (value: string) => dispatchFilter({ type: "SET_SELECTED_CATEGORY", payload: value })
  const setSelectedStockStatus = (value: StockStatus) => dispatchFilter({ type: "SET_SELECTED_STOCK_STATUS", payload: value })
  const handleSearch = () => dispatchFilter({ type: "RESET_PAGE" })
  const clearFilters = () => dispatchFilter({ type: "CLEAR_FILTERS" })
  
  // Modal actions
  const setIsAddModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_ADD" }) : dispatchModal({ type: "CLOSE_ADD" })
  const setIsEditModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_EDIT", payload: modalState.selectedProduct! }) : dispatchModal({ type: "CLOSE_EDIT" })
  const setIsDeleteModalOpen = (value: boolean) => value ? dispatchModal({ type: "OPEN_DELETE", payload: modalState.selectedProduct! }) : dispatchModal({ type: "CLOSE_DELETE" })
  const setSelectedProduct = (product: Product | null) => {
    if (product && modalState.isEditModalOpen) {
      dispatchModal({ type: "OPEN_EDIT", payload: product })
    } else if (product && modalState.isDeleteModalOpen) {
      dispatchModal({ type: "OPEN_DELETE", payload: product })
    }
  }
  const closeAddModal = () => dispatchModal({ type: "CLOSE_ADD" })
  const closeEditModal = () => dispatchModal({ type: "CLOSE_EDIT" })
  const closeDeleteModal = () => dispatchModal({ type: "CLOSE_DELETE" })
  
  // Mutation handlers - callbacks handle toasts
  const handleAddProduct = (data: ProductFormData) => {
    createProduct.mutate(data)
    closeAddModal()
  }
  
  const handleEditProduct = (data: ProductFormData) => {
    if (!modalState.selectedProduct) return
    updateProduct.mutate({ id: modalState.selectedProduct.id, data })
    closeEditModal()
  }
  
  const handleDeleteProduct = () => {
    if (!modalState.selectedProduct) return
    deleteProduct.mutate(modalState.selectedProduct.id)
    closeDeleteModal()
  }
  
  const openEditModal = (product: Product) => dispatchModal({ type: "OPEN_EDIT", payload: product })
  const openDeleteModal = (product: Product) => dispatchModal({ type: "OPEN_DELETE", payload: product })

  const hasActiveFilters = filterState.selectedCategory !== "ALL" || filterState.selectedStockStatus !== "ALL"
  
  return {
    // State
    timeFilter: filterState.timeFilter,
    setTimeFilter,
    searchQuery: filterState.searchQuery,
    setSearchQuery,
    currentPage: filterState.currentPage,
    setCurrentPage,
    pageSize: filterState.pageSize,
    
    // Filter states
    selectedCategory: filterState.selectedCategory,
    setSelectedCategory,
    selectedStockStatus: filterState.selectedStockStatus,
    setSelectedStockStatus,
    
    // Modal states
    isAddModalOpen: modalState.isAddModalOpen,
    setIsAddModalOpen,
    isEditModalOpen: modalState.isEditModalOpen,
    setIsEditModalOpen,
    isDeleteModalOpen: modalState.isDeleteModalOpen,
    setIsDeleteModalOpen,
    selectedProduct: modalState.selectedProduct,
    setSelectedProduct,
    
    // Data
    categories,
    products,
    totalElements,
    totalPages,
    isLoading,
    
    // Mutations
    createProduct,
    updateProduct,
    deleteProduct,
    
    // Handlers
    handleSearch,
    handleAddProduct,
    handleEditProduct,
    handleDeleteProduct,
    openEditModal,
    openDeleteModal,
    clearFilters,
    hasActiveFilters,
    closeAddModal,
    closeEditModal,
    closeDeleteModal,
  }
}
