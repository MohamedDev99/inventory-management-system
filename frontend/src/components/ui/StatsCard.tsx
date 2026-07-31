import { TrendingUp, TrendingDown, Minus } from "lucide-react"
import { cn } from "@/lib/utils"

interface StatsCardProps {
  title: string
  value: string | number
  trend?: number
  trendLabel?: string
  variant?: "primary" | "default"
  className?: string
}

export default function StatsCard({
  title,
  value,
  trend,
  trendLabel,
  variant = "default",
  className,
}: StatsCardProps) {
  const isPrimary = variant === "primary"

  const getTrendIcon = () => {
    if (trend === undefined || trend === 0) {
      return <Minus className="w-3 h-3" />
    }
    return trend > 0 ? (
      <TrendingUp className="w-3 h-3" />
    ) : (
      <TrendingDown className="w-3 h-3" />
    )
  }

  const getTrendColor = () => {
    if (trend === undefined || trend === 0) {
      return "text-primary-100 dark:text-primary-200"
    }
    return trend > 0
      ? "text-primary-100 dark:text-primary-200"
      : "text-red-100 dark:text-red-200"
  }

  return (
    <div
      className={cn(
        "rounded-lg p-4 shadow-md transition-transform duration-200 hover:scale-[1.02]",
        isPrimary
          ? "bg-primary-500 dark:bg-primary-600 text-white"
          : "bg-white dark:bg-accent-900 border border-accent-200 dark:border-accent-700",
        className
      )}
    >
      <p
        className={cn(
          "text-sm",
          isPrimary
            ? "text-primary-100 dark:text-primary-200"
            : "text-accent-500 dark:text-accent-400"
        )}
      >
        {title}
      </p>
      <p
        className={cn(
          "text-3xl font-bold mt-1",
          isPrimary ? "text-white" : "text-accent-900 dark:text-accent-100"
        )}
      >
        {value}
      </p>
      {trend !== undefined && (
        <div
          className={cn(
            "mt-2 flex items-center gap-1 text-sm",
            getTrendColor()
          )}
        >
          {getTrendIcon()}
          <span>
            {trend > 0 ? `↑ ${trend}%` : trend < 0 ? `↓ ${Math.abs(trend)}%` : "—"}
            {trendLabel && ` ${trendLabel}`}
          </span>
        </div>
      )}
    </div>
  )
}
