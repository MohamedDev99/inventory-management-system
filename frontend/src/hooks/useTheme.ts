import { useEffect, useState } from "react"
import { useAppStore } from "@/store/appStore"

type Theme = "light" | "dark" | "system"

export function useTheme() {
  const theme = useAppStore((state) => state.theme)
  const setTheme = useAppStore((state) => state.setTheme)
  const [mounted, setMounted] = useState(false)

  // Apply theme class when theme changes
  useEffect(() => {
    const root = window.document.documentElement
    
    if (theme === "dark") {
      root.classList.add("dark")
    } else if (theme === "light") {
      root.classList.remove("dark")
    } else if (theme === "system") {
      const systemTheme = window.matchMedia("(prefers-color-scheme: dark)").matches
        ? "dark"
        : "light"
      root.classList.toggle("dark", systemTheme === "dark")
    }
  }, [theme])

  // Mark as mounted after initial render
  useEffect(() => {
    setMounted(true)
  }, [])

  // Listen for system theme changes when using "system" theme
  useEffect(() => {
    if (theme !== "system") return

    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)")
    
    const handleChange = (e: MediaQueryListEvent) => {
      const root = window.document.documentElement
      root.classList.toggle("dark", e.matches)
    }

    mediaQuery.addEventListener("change", handleChange)
    return () => mediaQuery.removeEventListener("change", handleChange)
  }, [theme])

  const toggleTheme = () => {
    const newTheme = theme === "light" ? "dark" : "light"
    setTheme(newTheme)
  }

  const isDark = theme === "dark" || (
    theme === "system" && 
    typeof window !== "undefined" && 
    window.matchMedia("(prefers-color-scheme: dark)").matches
  )

  return {
    theme: theme as Theme,
    setTheme,
    toggleTheme,
    mounted,
    isDark,
  }
}
