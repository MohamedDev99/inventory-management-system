import React from "react"
import { useNavigate } from "react-router-dom"
import { Bell, AlertTriangle, CheckCircle, Truck, ArrowUpDown, Info } from "lucide-react"
import { Badge } from "@/components/ui/badge"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import { ScrollArea } from "@/components/ui/scroll-area"
// import {
//   useNotifications,
//   useUnreadNotificationCount,
//   useMarkNotificationAsRead,
//   useMarkAllNotificationsAsRead,
// } from "@/services/notification"
import type { Notification } from "@/types"

interface NotificationDropdownProps {
  children: React.ReactNode
}

export function NotificationDropdown({ children }: NotificationDropdownProps) {
  const navigate = useNavigate()
  const [open, setOpen] = React.useState(false)

  // Notifications API - commented out until backend is ready
  // const { data: notificationsData, refetch } = useNotifications({
  //   page: 0,
  //   size: 10,
  //   sort: "createdAt,desc",
  // })

  // const { data: unreadCountData, refetch: refetchUnread } = useUnreadNotificationCount()
  // const markAsRead = useMarkNotificationAsRead()
  // const markAllAsRead = useMarkAllNotificationsAsRead()

  // Placeholder data
  const notificationsData = null
  const refetch = () => {}
  const unreadCountData = null
  const refetchUnread = () => {}
  const markAsRead = { mutate: () => {} }
  const markAllAsRead = { mutate: () => {} }

  const notifications: Notification[] = notificationsData?.content || []
  const unreadCount = unreadCountData?.data?.unreadCount || 0

  const getTypeIcon = (type: string) => {
    const icons: Record<string, React.ReactNode> = {
      LOW_STOCK: <AlertTriangle className="w-5 h-5 text-yellow-500" />,
      ORDER_APPROVED: <CheckCircle className="w-5 h-5 text-green-500" />,
      ORDER_RECEIVED: <CheckCircle className="w-5 h-5 text-green-500" />,
      SHIPMENT: <Truck className="w-5 h-5 text-blue-500" />,
      STOCK_ADJUSTMENT: <ArrowUpDown className="w-5 h-5 text-purple-500" />,
      SYSTEM: <Info className="w-5 h-5 text-gray-500 dark:text-gray-400" />,
    }
    return icons[type] || <Info className="w-5 h-5 text-gray-500" />
  }

  const getPriorityBorder = (priority: string) => {
    const colors: Record<string, string> = {
      CRITICAL: "border-l-red-500 dark:border-l-red-400",
      HIGH: "border-l-orange-500 dark:border-l-orange-400",
      MEDIUM: "border-l-blue-500 dark:border-l-blue-400",
      LOW: "border-l-gray-400 dark:border-l-gray-500",
    }
    return colors[priority] || "border-l-gray-400"
  }

  const getRelativeTime = (dateString: string) => {
    const date = new Date(dateString)
    const now = new Date()
    const diffMs = now.getTime() - date.getTime()
    const diffMins = Math.floor(diffMs / 60000)
    const diffHours = Math.floor(diffMs / 3600000)
    const diffDays = Math.floor(diffMs / 86400000)

    if (diffMins < 1) return "Just now"
    if (diffMins < 60) return `${diffMins}m ago`
    if (diffHours < 24) return `${diffHours}h ago`
    if (diffDays < 7) return `${diffDays}d ago`
    return date.toLocaleDateString()
  }

  const handleMarkAsRead = async (id: number) => {
    try {
      await markAsRead.mutateAsync(id)
      refetch()
      refetchUnread()
    } catch (error) {
      console.error("Failed to mark as read:", error)
    }
  }

  const handleMarkAllAsRead = async () => {
    try {
      await markAllAsRead.mutateAsync()
      refetch()
      refetchUnread()
    } catch (error) {
      console.error("Failed to mark all as read:", error)
    }
  }

  const handleNotificationClick = (notification: Notification) => {
    if (!notification.isRead) {
      handleMarkAsRead(notification.id)
    }
    if (notification.actionUrl) {
      navigate(notification.actionUrl)
      setOpen(false)
    }
  }

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        {children}
      </PopoverTrigger>
      <PopoverContent className="w-[400px] p-0" align="start" side="bottom">
        <div className="flex items-center justify-between p-4 border-b dark:border-gray-700">
          <div className="flex items-center gap-2">
            <h2 className="font-semibold text-gray-900 dark:text-gray-100">Notifications</h2>
            {unreadCount > 0 && (
              <Badge className="bg-primary-500 text-white">{unreadCount} unread</Badge>
            )}
          </div>
          {unreadCount > 0 && (
            <button
              onClick={handleMarkAllAsRead}
              className="text-sm text-primary-500 hover:text-primary-600 dark:text-primary-400"
              disabled={markAllAsRead.isPending}
            >
              {markAllAsRead.isPending ? "Marking..." : "Mark all as read"}
            </button>
          )}
        </div>

        <ScrollArea className="h-[400px]">
          {notifications.length === 0 ? (
            <div className="flex flex-col items-center justify-center py-8 text-muted-foreground">
              <Bell className="w-12 h-12 mb-3 text-gray-300 dark:text-gray-600" />
              <p>No notifications</p>
            </div>
          ) : (
            <div className="divide-y dark:divide-gray-700">
              {notifications.map((notification) => (
                <div
                  key={notification.id}
                  onClick={() => handleNotificationClick(notification)}
                  className={`p-4 cursor-pointer hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors border-l-4 ${getPriorityBorder(notification.priority)} ${
                    !notification.isRead ? "bg-primary-50/50 dark:bg-primary-900/10" : ""
                  }`}
                >
                  <div className="flex items-start gap-3">
                    <div className="flex-shrink-0 mt-0.5">
                      {getTypeIcon(notification.type)}
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2">
                        <h3
                          className={`font-medium text-sm ${
                            !notification.isRead
                              ? "text-gray-900 dark:text-gray-100"
                              : "text-gray-700 dark:text-gray-300"
                          }`}
                        >
                          {notification.title}
                        </h3>
                        {!notification.isRead && (
                          <span className="w-2 h-2 bg-primary-500 rounded-full flex-shrink-0" />
                        )}
                      </div>
                      <p className="text-sm text-muted-foreground mt-0.5 line-clamp-2">
                        {notification.message}
                      </p>
                      <span className="text-xs text-muted-foreground mt-1 block">
                        {getRelativeTime(notification.createdAt)}
                      </span>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </ScrollArea>

        <div className="p-3 border-t dark:border-gray-700">
          <button
            onClick={() => {
              navigate("/notifications")
              setOpen(false)
            }}
            className="w-full text-center text-sm text-primary-500 hover:text-primary-600 dark:text-primary-400 font-medium"
          >
            View all notifications →
          </button>
        </div>
      </PopoverContent>
    </Popover>
  )
}
