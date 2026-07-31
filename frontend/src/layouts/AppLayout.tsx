import { useState } from "react"
import { Outlet } from "react-router-dom"
import Sidebar from "@/components/layout/Sidebar"
import Header from "@/components/layout/Header"
import { useAppStore } from "@/store/appStore"
import { useTheme } from "@/hooks/useTheme"

export default function AppLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const { sidebarCollapsed } = useAppStore()
  // Use useTheme to subscribe to theme changes and trigger re-renders
  useTheme()

  return (
    <div className="min-h-screen bg-white dark:bg-accent-950">
      {/* Sidebar */}
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      {/* Main Content */}
      <div
        className={`transition-all duration-300 ${
          sidebarCollapsed ? "lg:ml-20" : "lg:ml-[280px]"
        }`}
      >
        {/* Header */}
        <Header onMenuClick={() => setSidebarOpen(true)} />

        {/* Page Content */}
        <main className="p-4 lg:p-6">
          <Outlet />
        </main>
      </div>
    </div>
  )
}
