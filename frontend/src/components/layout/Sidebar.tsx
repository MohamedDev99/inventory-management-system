import { useEffect, useRef } from "react"
import { NavLink, useNavigate } from "react-router-dom"
import {
  LayoutDashboard,
  Package,
  Truck,
  Tags,
  Warehouse,
  Boxes,
  ShoppingCart,
  Receipt,
  Ship,
  Users,
  UserCog,
  Building2,
  CreditCard,
  FileText,
  Settings,
  LogOut,
  Search,
  ChevronLeft,
  ChevronRight,
  Bell,
} from "lucide-react"
import { cn } from "@/lib/utils"
import { useAuthStore } from "@/store/authStore"
import { useAppStore } from "@/store/appStore"
import { useTheme } from "@/hooks/useTheme"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { getInitials } from "@/lib/utils"
import { NotificationDropdown } from "./NotificationDropdown"
// import { useUnreadNotificationCount } from "@/services/notification"
import { APP_ROUTES, ENTITY_ROUTES } from "@/constants"

const navItems = [
  { icon: LayoutDashboard, label: "Dashboard", path: APP_ROUTES.DASHBOARD },
  { icon: Package, label: "Products", path: ENTITY_ROUTES.PRODUCTS },
  { icon: Truck, label: "Supplier", path: ENTITY_ROUTES.SUPPLIERS },
  { icon: Tags, label: "Category", path: ENTITY_ROUTES.CATEGORIES },
  { icon: Warehouse, label: "Warehouse", path: ENTITY_ROUTES.WAREHOUSES },
  { icon: Boxes, label: "Stock", path: ENTITY_ROUTES.STOCK },
  { icon: ShoppingCart, label: "Purchase Order", path: ENTITY_ROUTES.PURCHASE_ORDERS },
  { icon: Receipt, label: "Sales Order", path: ENTITY_ROUTES.SALES_ORDERS },
  { icon: Ship, label: "Shipment", path: ENTITY_ROUTES.SHIPMENTS },
  { icon: Users, label: "Customer", path: ENTITY_ROUTES.CUSTOMERS },
  { icon: UserCog, label: "Employee", path: ENTITY_ROUTES.EMPLOYEES },
  { icon: Building2, label: "Department", path: ENTITY_ROUTES.DEPARTMENTS },
  { icon: CreditCard, label: "Payment", path: ENTITY_ROUTES.PAYMENTS },
  { icon: FileText, label: "Invoice", path: ENTITY_ROUTES.INVOICES },
  { icon: FileText, label: "Reports", path: APP_ROUTES.REPORTS },
  { icon: Bell, label: "Notifications", path: APP_ROUTES.NOTIFICATIONS },
]

interface SidebarProps {
  isOpen: boolean
  onClose: () => void
}

export default function Sidebar({ isOpen, onClose }: SidebarProps) {
  const navigate = useNavigate()
  const { user, logout } = useAuthStore()
  const { sidebarCollapsed, setSidebarCollapsed } = useAppStore()
  const pollingIntervalRef = useRef<number | null>(null)
  
  // Use useTheme to subscribe to theme changes and trigger re-renders
  useTheme()

  // Fetch unread notification count with polling - commented out until backend is ready
  // const { data: unreadCountData, refetch } = useUnreadNotificationCount()
  
  // Placeholder for notification count
  const unreadCountData = null
  const refetch = () => {}
  
  useEffect(() => {
    // Poll every 60 seconds - disabled until backend is ready
    // pollingIntervalRef.current = window.setInterval(() => {
    //   refetch()
    // }, 60000)

    return () => {
      if (pollingIntervalRef.current) {
        clearInterval(pollingIntervalRef.current)
      }
    }
  }, [refetch])

  const unreadCount = unreadCountData?.data?.unreadCount || 0

  const handleLogout = () => {
    logout()
    navigate("/login")
  }

  const getBadgeColor = () => {
    return "bg-red-500"
  }

  return (
    <>
      {/* Mobile Overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-40 lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          "fixed top-0 left-0 z-50 h-screen bg-white dark:bg-accent-950 border-r border-accent-200 dark:border-accent-800 transition-all duration-300",
          "flex flex-col",
          sidebarCollapsed ? "w-20" : "w-[280px]",
          isOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"
        )}
      >
        {/* Logo */}
        <div className="flex items-center justify-between h-16 px-4 border-b border-accent-200 dark:border-accent-800">
          <div className="flex items-center gap-3">
            <div className="bg-gradient-to-br from-primary-600 to-primary-400 rounded-lg p-2">
              <Package className="w-5 h-5 text-white" />
            </div>
            {!sidebarCollapsed && (
              <span className="font-display text-xl font-bold bg-gradient-to-r from-primary-700 to-primary-500 bg-clip-text text-transparent">
                MoeWare
              </span>
            )}
          </div>
          <Button
            variant="ghost"
            size="icon"
            className="hidden lg:flex"
            onClick={() => setSidebarCollapsed(!sidebarCollapsed)}
          >
            {sidebarCollapsed ? (
              <ChevronRight className="w-5 h-5" />
            ) : (
              <ChevronLeft className="w-5 h-5" />
            )}
          </Button>
        </div>

        {/* Notification Bell */}
        <div className="px-4 py-3 border-b border-accent-200 dark:border-accent-800">
          <NotificationDropdown>
            <button
              className={cn(
                "relative flex items-center justify-center w-full py-2 rounded-lg hover:bg-accent-100 dark:hover:bg-accent-800 transition-colors",
                sidebarCollapsed ? "px-0" : "px-3"
              )}
            >
              <Bell className="w-5 h-5 text-accent-600 dark:text-accent-400" />
              {unreadCount > 0 && (
                <span
                  className={cn(
                    "absolute flex items-center justify-center min-w-[18px] h-[18px] text-[10px] font-bold text-white rounded-full",
                    getBadgeColor(),
                    sidebarCollapsed ? "right-0 top-0" : "right-2 top-0"
                  )}
                >
                  {unreadCount > 99 ? "99+" : unreadCount}
                </span>
              )}
              {!sidebarCollapsed && (
                <span className="ml-3 text-sm font-medium text-accent-600 dark:text-accent-400">
                  Notifications
                </span>
              )}
            </button>
          </NotificationDropdown>
        </div>

        {/* Search */}
        <div className="p-4">
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-accent-400" />
            {!sidebarCollapsed && (
              <Input
                placeholder="Search..."
                className="pl-9 bg-accent-50 dark:bg-accent-800 border-accent-200 dark:border-accent-700"
              />
            )}
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 overflow-y-auto px-3 py-2">
          <ul className="space-y-1">
            {navItems.map((item) => (
              <li key={item.path}>
                <NavLink
                  to={item.path}
                  className={({ isActive }) =>
                    cn(
                      "flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors",
                      isActive
                        ? "bg-primary-500 text-white"
                        : "text-accent-600 dark:text-accent-400 hover:bg-accent-100 dark:hover:bg-accent-800 hover:text-accent-900 dark:hover:text-accent-100",
                      sidebarCollapsed && "justify-center"
                    )
                  }
                  onClick={onClose}
                >
                  <item.icon className="w-5 h-5 flex-shrink-0" />
                  {!sidebarCollapsed && (
                    <span className="text-sm font-medium">{item.label}</span>
                  )}
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>

        {/* Settings */}
        <div className="px-3 py-2 border-t border-accent-200 dark:border-accent-800">
          <NavLink
            to={APP_ROUTES.SETTINGS}
            className={({ isActive }) =>
              cn(
                "flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors",
                isActive
                  ? "bg-primary-500 text-white"
                  : "text-accent-600 dark:text-accent-400 hover:bg-accent-100 dark:hover:bg-accent-800 hover:text-accent-900 dark:hover:text-accent-100",
                sidebarCollapsed && "justify-center"
              )
            }
            onClick={onClose}
          >
            <Settings className="w-5 h-5 flex-shrink-0" />
            {!sidebarCollapsed && (
              <span className="text-sm font-medium">Settings</span>
            )}
          </NavLink>
        </div>

        {/* User Profile */}
        <div className="p-4 border-t border-accent-200 dark:border-accent-800">
          <div
            className={cn(
              "flex items-center gap-3",
              sidebarCollapsed && "justify-center"
            )}
          >
            <Avatar className="w-10 h-10">
              <AvatarImage src={user?.email} />
              <AvatarFallback className="bg-primary-100 dark:bg-primary-900 text-primary-600 dark:text-primary-300">
                {user?.username ? getInitials(user.username) : "U"}
              </AvatarFallback>
            </Avatar>
            {!sidebarCollapsed && (
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-accent-900 dark:text-accent-100 truncate">
                  {user?.username || "User"}
                </p>
                <p className="text-xs text-accent-500 truncate">
                  {user?.roleName || "Admin"}
                </p>
              </div>
            )}
            {!sidebarCollapsed && (
              <Button
                variant="ghost"
                size="icon"
                onClick={handleLogout}
                className="text-accent-400 hover:text-accent-600 dark:hover:text-accent-300"
              >
                <LogOut className="w-4 h-4" />
              </Button>
            )}
          </div>
        </div>
      </aside>
    </>
  )
}
