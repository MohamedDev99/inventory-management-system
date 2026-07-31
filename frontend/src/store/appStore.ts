import { create } from "zustand"
import { persist } from "zustand/middleware"

type Theme = "light" | "dark" | "system"
type SidebarDensity = "comfortable" | "compact"
type TableDensity = "default" | "compact" | "spacious"

interface AppState {
  // Theme
  theme: Theme
  setTheme: (theme: Theme) => void
  
  // Sidebar
  sidebarOpen: boolean
  sidebarCollapsed: boolean
  sidebarDensity: SidebarDensity
  setSidebarOpen: (open: boolean) => void
  setSidebarCollapsed: (collapsed: boolean) => void
  setSidebarDensity: (density: SidebarDensity) => void
  
  // Table
  tableDensity: TableDensity
  setTableDensity: (density: TableDensity) => void
  
  // Notifications
  unreadCount: number
  setUnreadCount: (count: number) => void
}

export const useAppStore = create<AppState>()(
  persist(
    (set) => ({
      // Theme
      theme: "light",
      setTheme: (theme) => set({ theme }),
      
      // Sidebar
      sidebarOpen: true,
      sidebarCollapsed: false,
      sidebarDensity: "comfortable",
      setSidebarOpen: (open) => set({ sidebarOpen: open }),
      setSidebarCollapsed: (collapsed) => set({ sidebarCollapsed: collapsed }),
      setSidebarDensity: (density) => set({ sidebarDensity: density }),
      
      // Table
      tableDensity: "default",
      setTableDensity: (density) => set({ tableDensity: density }),
      
      // Notifications
      unreadCount: 0,
      setUnreadCount: (count) => set({ unreadCount: count }),
    }),
    {
      name: "app-storage",
    }
  )
)
