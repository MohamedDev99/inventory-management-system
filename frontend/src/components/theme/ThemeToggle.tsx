import { Moon, Sun } from "lucide-react"
import { useTheme } from "@/hooks/useTheme"
import { Button } from "@/components/ui/button"

export function ThemeToggle() {
  const { theme, toggleTheme, mounted } = useTheme()

  if (!mounted) {
    return (
      <Button variant="ghost" size="icon" className="h-9 w-9">
        <Sun className="h-4 w-4" />
      </Button>
    )
  }

  return (
    <Button
      variant="ghost"
      size="icon"
      className="h-9 w-9"
      onClick={toggleTheme}
      title={`Switch to ${theme === "dark" ? "light" : "dark"} mode`}
    >
      {theme === "dark" ? (
        <Moon className="h-4 w-4" />
      ) : (
        <Sun className="h-4 w-4" />
      )}
    </Button>
  )
}

export function ThemeSwitcher() {
  const { theme, setTheme, mounted } = useTheme()

  if (!mounted) {
    return null
  }

  return (
    <div className="flex items-center gap-1 bg-accent-100 dark:bg-accent-800 rounded-lg p-1">
      <Button
        variant={theme === "light" ? "default" : "ghost"}
        size="sm"
        className="h-7 px-3"
        onClick={() => setTheme("light")}
      >
        <Sun className="h-3 w-3 mr-1.5" />
        Light
      </Button>
      <Button
        variant={theme === "dark" ? "default" : "ghost"}
        size="sm"
        className="h-7 px-3"
        onClick={() => setTheme("dark")}
      >
        <Moon className="h-3 w-3 mr-1.5" />
        Dark
      </Button>
      <Button
        variant={theme === "system" ? "default" : "ghost"}
        size="sm"
        className="h-7 px-3"
        onClick={() => setTheme("system")}
      >
        <span className="text-xs mr-1.5">💻</span>
        System
      </Button>
    </div>
  )
}
