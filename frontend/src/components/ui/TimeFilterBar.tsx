import { Calendar } from "lucide-react"
import { cn } from "@/lib/utils"

export type TimeFilterValue = "1d" | "7d" | "1m" | "3m" | "6m" | "1y" | "3y" | "5y"

interface TimeFilterBarProps {
  value: TimeFilterValue
  onChange: (value: TimeFilterValue) => void
  showSelectDates?: boolean
  onSelectDates?: () => void
  className?: string
}

const timeFilters: TimeFilterValue[] = ["1d", "7d", "1m", "3m", "6m", "1y", "3y", "5y"]

export default function TimeFilterBar({
  value,
  onChange,
  showSelectDates = true,
  onSelectDates,
  className,
}: TimeFilterBarProps) {
  return (
    <div
      className={cn(
        "flex items-center gap-1 bg-gray-100 dark:bg-accent-800 p-1 rounded-lg w-fit",
        className
      )}
    >
      {timeFilters.map((filter) => (
        <button
          key={filter}
          onClick={() => onChange(filter)}
          className={cn(
            "px-3 py-1.5 text-sm rounded-md transition-colors",
            value === filter
              ? "bg-white dark:bg-accent-700 text-primary-500 dark:text-primary-400 shadow-sm font-medium"
              : "text-accent-500 dark:text-accent-400 hover:text-accent-900 dark:hover:text-accent-100"
          )}
        >
          {filter}
        </button>
      ))}
      
      {showSelectDates && (
        <button
          onClick={onSelectDates}
          className="px-3 py-1.5 text-sm text-accent-500 dark:text-accent-400 hover:text-accent-900 dark:hover:text-accent-100 flex items-center gap-1"
        >
          <Calendar className="w-3 h-3" />
          Select dates
        </button>
      )}
    </div>
  )
}

export { timeFilters }
