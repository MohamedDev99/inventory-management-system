import { Menu, Bell, Code } from "lucide-react"
import { Button } from "@/components/ui"
import { useAppStore } from "@/store/appStore"
import { ThemeToggle } from "@/components/theme/ThemeToggle"
import { useTheme } from "@/hooks/useTheme"

interface HeaderProps {
  onMenuClick: () => void
}

export default function Header({ onMenuClick }: HeaderProps) {
  const { unreadCount } = useAppStore()
  // Use useTheme to subscribe to theme changes and trigger re-renders
  useTheme()

  return (
    <header className="sticky top-0 z-30 h-16 bg-white dark:bg-accent-950 border-b border-accent-200 dark:border-accent-800">
      <div className="flex items-center justify-between h-full px-4 lg:px-6">
        {/* Left - Menu Button (Mobile) */}
        <div className="flex items-center gap-4">
          <Button
            variant="ghost"
            size="icon"
            className="lg:hidden"
            onClick={onMenuClick}
          >
            <Menu className="w-5 h-5" />
          </Button>
        </div>

        {/* Right - Actions */}
        <div className="flex items-center gap-2">
          {/* Theme Toggle */}
          <ThemeToggle />

          {/* Dev Toggle */}
          <Button variant="ghost" size="icon" title="Developer Tools">
            <Code className="w-5 h-5 text-accent-500" />
          </Button>

          {/* Notifications */}
          <Button variant="ghost" size="icon" className="relative" title="Notifications">
            <Bell className="w-5 h-5 text-accent-600 dark:text-accent-400" />
            {unreadCount > 0 && (
              <span className="absolute -top-1 -right-1 w-5 h-5 bg-error-500 text-white text-xs font-medium rounded-full flex items-center justify-center">
                {unreadCount > 99 ? "99+" : unreadCount}
              </span>
            )}
          </Button>
        </div>
      </div>
    </header>
  )
}
