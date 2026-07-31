import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { useNotifications } from "./useNotifications"

export default function Notifications() {
  const navigate = useNavigate()
  const {
    searchQuery,
    setSearchQuery,
    typeFilter,
    setTypeFilter,
    priorityFilter,
    setPriorityFilter,
    statusFilter,
    setStatusFilter,
    startDate,
    setStartDate,
    endDate,
    setEndDate,
    selectedNotifications,
    setSelectedNotifications,
    totalElements,
    unreadCount,
    isLoading,
    filteredNotifications,
    handleMarkAsRead,
    handleMarkAllAsRead,
    handleDelete,
    handleDeleteSelected,
    handleClearRead,
    getTypeIcon,
    getPriorityColor,
    getTypeBadge,
  } = useNotifications()

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center gap-2 text-sm text-muted-foreground">
        <button onClick={() => navigate(-1)} className="hover:text-primary-500 dark:hover:text-primary-400">← Back</button>
        <span>/ Notifications</span>
      </div>
      <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Notifications</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-primary-50 dark:bg-primary-900/30 border border-primary-200 dark:border-primary-800 rounded-lg p-4">
          <p className="text-sm text-primary-600 dark:text-primary-400">Total Notifications</p>
          <p className="text-2xl font-bold text-primary-700 dark:text-primary-300">{totalElements}</p>
        </div>
        <div className="bg-primary-50 dark:bg-primary-900/30 border border-primary-200 dark:border-primary-800 rounded-lg p-4">
          <p className="text-sm text-primary-600 dark:text-primary-400">Unread</p>
          <p className="text-2xl font-bold text-primary-700 dark:text-primary-300">{unreadCount}</p>
        </div>
        <div className="bg-red-50 dark:bg-red-900/30 border border-red-200 dark:border-red-800 rounded-lg p-4">
          <p className="text-sm text-red-600 dark:text-red-400">Critical Alerts</p>
          <p className="text-2xl font-bold text-red-700 dark:text-red-300">0</p>
        </div>
      </div>

      <div className="flex items-center justify-between gap-4 flex-wrap">
        <div className="flex items-center gap-3 flex-wrap">
          <div className="relative">
            <Input 
              type="text" 
              placeholder="Search notifications..." 
              value={searchQuery} 
              onChange={(e) => setSearchQuery(e.target.value)} 
              className="w-64 pl-10 bg-white dark:bg-accent-content border-accent-200 dark:border-accent-700 text-accent-900 dark:text-accent-100" 
            />
          </div>
          
          <Select value={typeFilter} onValueChange={(value) => setTypeFilter(value as typeof typeFilter)}>
            <SelectTrigger className="w-[160px] bg-white dark:bg-accent-content">
              <SelectValue placeholder="Filter by Type" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">All Types</SelectItem>
              <SelectItem value="LOW_STOCK">Low Stock</SelectItem>
              <SelectItem value="ORDER_APPROVED">Order Approved</SelectItem>
              <SelectItem value="ORDER_RECEIVED">Order Received</SelectItem>
              <SelectItem value="SHIPMENT">Shipment</SelectItem>
              <SelectItem value="STOCK_ADJUSTMENT">Stock Adjustment</SelectItem>
              <SelectItem value="SYSTEM">System</SelectItem>
            </SelectContent>
          </Select>

          <Select value={priorityFilter} onValueChange={(value) => setPriorityFilter(value as typeof priorityFilter)}>
            <SelectTrigger className="w-[160px] bg-white dark:bg-accent-content">
              <SelectValue placeholder="Filter by Priority" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">All Priorities</SelectItem>
              <SelectItem value="CRITICAL">Critical</SelectItem>
              <SelectItem value="HIGH">High</SelectItem>
              <SelectItem value="MEDIUM">Medium</SelectItem>
              <SelectItem value="LOW">Low</SelectItem>
            </SelectContent>
          </Select>

          <Select value={statusFilter} onValueChange={(value) => setStatusFilter(value as typeof statusFilter)}>
            <SelectTrigger className="w-[140px] bg-white dark:bg-accent-content">
              <SelectValue placeholder="Status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">All</SelectItem>
              <SelectItem value="UNREAD">Unread</SelectItem>
              <SelectItem value="READ">Read</SelectItem>
            </SelectContent>
          </Select>

          <div className="flex items-center gap-2">
            <Input 
              type="date" 
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="w-[140px] bg-white dark:bg-accent-content"
              placeholder="Start"
            />
            <span className="text-muted-foreground">-</span>
            <Input 
              type="date" 
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="w-[140px] bg-white dark:bg-accent-content"
              placeholder="End"
            />
          </div>
        </div>
        
        <div className="flex items-center gap-3">
          <Button variant="outline" size="sm" onClick={handleMarkAllAsRead}>
            Mark all as read
          </Button>
          <Button 
            variant="outline" 
            size="sm" 
            className="text-red-500 hover:text-red-600 dark:text-red-400 dark:hover:text-red-300" 
            onClick={handleClearRead}
          >
            Clear read
          </Button>
        </div>
      </div>

      {selectedNotifications.length > 0 && (
        <div className="bg-primary-50 dark:bg-primary-900/30 border border-primary-200 dark:border-primary-800 rounded-lg p-3 flex items-center justify-between">
          <span className="text-sm font-medium text-primary-700 dark:text-primary-300">{selectedNotifications.length} selected</span>
          <div className="flex gap-2">
            <Button size="sm" variant="outline" onClick={() => selectedNotifications.forEach(id => handleMarkAsRead(id))}>Mark as read</Button>
            <Button size="sm" variant="destructive" onClick={handleDeleteSelected}>Delete</Button>
          </div>
        </div>
      )}

      <div className="space-y-2">
        {isLoading ? [...Array(5)].map((_, i) => (
          <div key={i} className="h-20 bg-gray-100 dark:bg-gray-800 rounded-lg animate-pulse" />
        )) : filteredNotifications.length === 0 ? (
          <div className="text-center py-12 text-muted-foreground">
            <svg className="w-12 h-12 mx-auto mb-4 text-gray-300 dark:text-gray-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
            </svg>
            <p>No notifications found.</p>
          </div>
        ) : filteredNotifications.map((notification) => (
          <div 
            key={notification.id} 
            className={`bg-white dark:bg-accent-content border dark:border-gray-700 rounded-lg p-4 hover:shadow-sm transition-shadow border-l-4 ${getPriorityColor(notification.priority)} ${!notification.isRead ? "bg-primary-50 dark:bg-primary-900/20" : ""}`}
          >
            <div className="flex items-start gap-4">
              <input 
                type="checkbox" 
                checked={selectedNotifications.includes(notification.id)} 
                onChange={(e) => {
                  if (e.target.checked) {
                    setSelectedNotifications([...selectedNotifications, notification.id])
                  } else {
                    setSelectedNotifications(selectedNotifications.filter(id => id !== notification.id))
                  }
                }}
                className="mt-1"
              />
              <div className={`mt-1 ${getTypeIcon(notification.notificationType)}`}>
                <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clipRule="evenodd" />
                </svg>
              </div>
              <div className="flex-1">
                <div className="flex items-center gap-2">
                  <h3 className={`font-medium ${!notification.isRead ? "text-gray-900 dark:text-gray-100" : "text-gray-700 dark:text-gray-300"}`}>
                    {notification.title}
                  </h3>
                  {!notification.isRead && <span className="w-2 h-2 bg-primary-500 rounded-full" />}
                </div>
                <p className="text-sm text-muted-foreground mt-1">{notification.message}</p>
                <div className="flex items-center gap-2 mt-2">
                  <Badge className={getTypeBadge(notification.notificationType)}>{notification.notificationType.replace("_", " ")}</Badge>
                  <Badge variant="outline" className="dark:border-gray-600 dark:text-gray-300">{notification.priority}</Badge>
                  <span className="text-xs text-muted-foreground">{new Date(notification.createdAt).toLocaleString()}</span>
                </div>
              </div>
              <div className="flex items-center gap-1">
                {!notification.isRead && (
                  <Button variant="ghost" size="sm" onClick={() => handleMarkAsRead(notification.id)}>
                    Mark as read
                  </Button>
                )}
                <Button 
                  variant="ghost" 
                  size="icon" 
                  className="h-8 w-8 text-red-500 hover:text-red-600 dark:text-red-400 dark:hover:text-red-300"
                  onClick={() => handleDelete(notification.id)}
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                  </svg>
                </Button>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
