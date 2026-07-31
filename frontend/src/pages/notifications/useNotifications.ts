import { useMemo, useReducer } from "react"
// import {
//   useNotifications as useNotificationsQuery,
//   useUnreadNotificationCount,
//   useMarkNotificationAsRead,
//   useMarkAllNotificationsAsRead,
//   useDeleteNotification,
//   useClearReadNotifications,
// } from "@/services/notification"
import type { Notification } from "@/types"

type NotificationTypeFilter = "ALL" | "LOW_STOCK" | "ORDER_APPROVED" | "ORDER_RECEIVED" | "SHIPMENT" | "STOCK_ADJUSTMENT" | "SYSTEM"
type NotificationPriorityFilter = "ALL" | "CRITICAL" | "HIGH" | "MEDIUM" | "LOW"
type NotificationStatusFilter = "ALL" | "UNREAD" | "READ"

// Consolidate filter state
interface FilterState {
  searchQuery: string
  currentPage: number
  pageSize: number
  typeFilter: NotificationTypeFilter
  priorityFilter: NotificationPriorityFilter
  statusFilter: NotificationStatusFilter
  startDate: string
  endDate: string
}

type FilterAction =
  | { type: "SET_SEARCH_QUERY"; payload: string }
  | { type: "SET_CURRENT_PAGE"; payload: number }
  | { type: "SET_TYPE_FILTER"; payload: NotificationTypeFilter }
  | { type: "SET_PRIORITY_FILTER"; payload: NotificationPriorityFilter }
  | { type: "SET_STATUS_FILTER"; payload: NotificationStatusFilter }
  | { type: "SET_START_DATE"; payload: string }
  | { type: "SET_END_DATE"; payload: string }
  | { type: "RESET_FILTERS" }

function filterReducer(state: FilterState, action: FilterAction): FilterState {
  switch (action.type) {
    case "SET_SEARCH_QUERY":
      return { ...state, searchQuery: action.payload, currentPage: 0 }
    case "SET_CURRENT_PAGE":
      return { ...state, currentPage: action.payload }
    case "SET_TYPE_FILTER":
      return { ...state, typeFilter: action.payload, currentPage: 0 }
    case "SET_PRIORITY_FILTER":
      return { ...state, priorityFilter: action.payload, currentPage: 0 }
    case "SET_STATUS_FILTER":
      return { ...state, statusFilter: action.payload, currentPage: 0 }
    case "SET_START_DATE":
      return { ...state, startDate: action.payload }
    case "SET_END_DATE":
      return { ...state, endDate: action.payload }
    case "RESET_FILTERS":
      return { ...state, typeFilter: "ALL", priorityFilter: "ALL", statusFilter: "ALL", startDate: "", endDate: "", currentPage: 0 }
    default:
      return state
  }
}

// Consolidate selection state
interface SelectionState {
  selectedNotifications: number[]
}

type SelectionAction =
  | { type: "SET_SELECTED"; payload: number[] }
  | { type: "TOGGLE_ONE"; payload: number }
  | { type: "CLEAR_ALL" }

function selectionReducer(state: SelectionState, action: SelectionAction): SelectionState {
  switch (action.type) {
    case "SET_SELECTED":
      return { ...state, selectedNotifications: action.payload }
    case "TOGGLE_ONE":
      return {
        ...state,
        selectedNotifications: state.selectedNotifications.includes(action.payload)
          ? state.selectedNotifications.filter(id => id !== action.payload)
          : [...state.selectedNotifications, action.payload]
      }
    case "CLEAR_ALL":
      return { ...state, selectedNotifications: [] }
    default:
      return state
  }
}

interface UseNotificationsReturn {
  // State
  searchQuery: string
  setSearchQuery: (value: string) => void
  currentPage: number
  pageSize: number
  
  // Filters
  typeFilter: NotificationTypeFilter
  setTypeFilter: (value: NotificationTypeFilter) => void
  priorityFilter: NotificationPriorityFilter
  setPriorityFilter: (value: NotificationPriorityFilter) => void
  statusFilter: NotificationStatusFilter
  setStatusFilter: (value: NotificationStatusFilter) => void
  startDate: string
  setStartDate: (value: string) => void
  endDate: string
  setEndDate: (value: string) => void
  
  // Selection
  selectedNotifications: number[]
  setSelectedNotifications: (ids: number[]) => void
  
  // Data
  notifications: Notification[]
  totalElements: number
  unreadCount: number
  isLoading: boolean
  filteredNotifications: Notification[]
  
  // Mutations
  markAsRead: ReturnType<typeof useMarkNotificationAsRead>
  markAllAsRead: ReturnType<typeof useMarkAllNotificationsAsRead>
  deleteNotification: ReturnType<typeof useDeleteNotification>
  clearReadNotifications: ReturnType<typeof useClearReadNotifications>
  
  // Handlers
  handleMarkAsRead: (id: number) => void
  handleMarkAllAsRead: () => void
  handleDelete: (id: number) => void
  handleDeleteSelected: () => void
  handleClearRead: () => void
  getTypeIcon: (type: string) => string
  getPriorityColor: (priority: string) => string
  getTypeBadge: (type: string) => string
}

export function useNotifications(): UseNotificationsReturn {
  // Consolidated filter state
  const [filterState, dispatchFilter] = useReducer(filterReducer, {
    searchQuery: "",
    currentPage: 0,
    pageSize: 20,
    typeFilter: "ALL",
    priorityFilter: "ALL",
    statusFilter: "ALL",
    startDate: "",
    endDate: "",
  })
  
  // Consolidated selection state
  const [selectionState, dispatchSelection] = useReducer(selectionReducer, {
    selectedNotifications: [],
  })
  
  // React Query hooks - commented out until backend is ready
  // const { data: notificationsData, isLoading, refetch } = useNotificationsQuery({
  //   page: filterState.currentPage,
  //   size: filterState.pageSize,
  //   search: filterState.searchQuery,
  // })
  
  // const { data: unreadCountData } = useUnreadNotificationCount()
  
  // const markAsRead = useMarkNotificationAsRead()
  // const markAllAsRead = useMarkAllNotificationsAsRead()
  // const deleteNotification = useDeleteNotification()
  // const clearReadNotifications = useClearReadNotifications()
  
  // Placeholder data for now
  const notificationsData = null
  const isLoading = false
  const refetch = () => {}
  
  const unreadCountData = null
  
  const markAsRead = { mutate: () => {} }
  const markAllAsRead = { mutate: () => {} }
  const deleteNotification = { mutate: () => {} }
  const clearReadNotifications = { mutate: () => {} }
  
  const notifications = useMemo(() => notificationsData?.data?.content || [], [notificationsData])
  const totalElements = notificationsData?.data?.totalElements || 0
  const unreadCount = unreadCountData?.data?.unreadCount || 0
  
  // Filter notifications client-side
  const filteredNotifications = useMemo(() => {
    return notifications.filter((notification: Notification) => {
      if (filterState.typeFilter !== "ALL" && notification.notificationType !== filterState.typeFilter) return false
      if (filterState.priorityFilter !== "ALL" && notification.priority !== filterState.priorityFilter) return false
      if (filterState.statusFilter === "UNREAD" && notification.isRead) return false
      if (filterState.statusFilter === "READ" && !notification.isRead) return false
      return true
    })
  }, [notifications, filterState.typeFilter, filterState.priorityFilter, filterState.statusFilter])
  
  // Filter actions
  const setSearchQuery = (value: string) => dispatchFilter({ type: "SET_SEARCH_QUERY", payload: value })
  const setTypeFilter = (value: NotificationTypeFilter) => dispatchFilter({ type: "SET_TYPE_FILTER", payload: value })
  const setPriorityFilter = (value: NotificationPriorityFilter) => dispatchFilter({ type: "SET_PRIORITY_FILTER", payload: value })
  const setStatusFilter = (value: NotificationStatusFilter) => dispatchFilter({ type: "SET_STATUS_FILTER", payload: value })
  const setStartDate = (value: string) => dispatchFilter({ type: "SET_START_DATE", payload: value })
  const setEndDate = (value: string) => dispatchFilter({ type: "SET_END_DATE", payload: value })
  
  // Selection actions
  const setSelectedNotifications = (ids: number[]) => dispatchSelection({ type: "SET_SELECTED", payload: ids })
  
  // Handlers - callbacks handle toasts in mutation services
  const handleMarkAsRead = (id: number) => markAsRead.mutate(id)
  const handleMarkAllAsRead = () => markAllAsRead.mutate(undefined)
  const handleDelete = (id: number) => deleteNotification.mutate(id)
  
  const handleDeleteSelected = () => {
    selectionState.selectedNotifications.forEach(id => {
      deleteNotification.mutate(id)
    })
    dispatchSelection({ type: "CLEAR_ALL" })
  }
  
  const handleClearRead = () => {
    clearReadNotifications.mutate(undefined, {
      onSuccess: () => refetch()
    })
  }
  
  const getTypeIcon = (type: string) => {
    const icons: Record<string, string> = {
      LOW_STOCK: "text-yellow-500",
      ORDER_APPROVED: "text-green-500",
      ORDER_RECEIVED: "text-green-500",
      SHIPMENT: "text-blue-500",
      STOCK_ADJUSTMENT: "text-blue-500",
      SYSTEM: "text-gray-500 dark:text-gray-400",
    }
    return icons[type] || "text-gray-500 dark:text-gray-400"
  }
  
  const getPriorityColor = (priority: string) => {
    const colors: Record<string, string> = { 
      CRITICAL: "border-l-red-500 dark:border-l-red-400", 
      HIGH: "border-l-orange-500 dark:border-l-orange-400", 
      MEDIUM: "border-l-blue-500 dark:border-l-blue-400", 
      LOW: "border-l-gray-400 dark:border-l-gray-500" 
    }
    return colors[priority] || "border-l-gray-400 dark:border-l-gray-500"
  }
  
  const getTypeBadge = (type: string) => {
    const colors: Record<string, string> = {
      LOW_STOCK: "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-300",
      ORDER_APPROVED: "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-300",
      ORDER_RECEIVED: "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-300",
      SHIPMENT: "bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300",
      STOCK_ADJUSTMENT: "bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-300",
      SYSTEM: "bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300",
    }
    return colors[type] || "bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300"
  }
  
  return {
    // State
    searchQuery: filterState.searchQuery,
    setSearchQuery,
    currentPage: filterState.currentPage,
    pageSize: filterState.pageSize,
    
    // Filters
    typeFilter: filterState.typeFilter,
    setTypeFilter,
    priorityFilter: filterState.priorityFilter,
    setPriorityFilter,
    statusFilter: filterState.statusFilter,
    setStatusFilter,
    startDate: filterState.startDate,
    setStartDate,
    endDate: filterState.endDate,
    setEndDate,
    
    // Selection
    selectedNotifications: selectionState.selectedNotifications,
    setSelectedNotifications,
    
    // Data
    notifications,
    totalElements,
    unreadCount,
    isLoading,
    filteredNotifications,
    
    // Mutations
    markAsRead,
    markAllAsRead,
    deleteNotification,
    clearReadNotifications,
    
    // Handlers
    handleMarkAsRead,
    handleMarkAllAsRead,
    handleDelete,
    handleDeleteSelected,
    handleClearRead,
    getTypeIcon,
    getPriorityColor,
    getTypeBadge,
  }
}
