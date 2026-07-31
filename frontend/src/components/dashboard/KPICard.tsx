import { TrendingUp, TrendingDown, MoreVertical } from "lucide-react"
import { cn } from "@/lib/utils"
import { Card, CardContent } from "@/components/ui"

interface KPICardProps {
  title: string
  value: string
  trend?: number
  trendLabel?: string
  sparklineData?: number[]
  icon?: React.ReactNode
}

export default function KPICard({
  title,
  value,
  trend,
  trendLabel,
  sparklineData,
  icon,
}: KPICardProps) {
  const isPositive = trend && trend >= 0
  const isNeutral = !trend || trend === 0

  // Generate sparkline path
  const generateSparkline = (data: number[]) => {
    if (!data || data.length === 0) return ""
    
    const max = Math.max(...data)
    const min = Math.min(...data)
    const range = max - min || 1
    
    const width = 100
    const height = 40
    const step = width / (data.length - 1)
    
    const points = data.map((val, i) => {
      const x = i * step
      const y = height - ((val - min) / range) * height
      return `${x},${y}`
    })
    
    return `M${points.join(" L")}`
  }

  return (
    <Card className="hover:shadow-md transition-shadow duration-200 dark:bg-accent-900 dark:border-accent-800">
      <CardContent className="p-4">
        <div className="flex items-start justify-between mb-3">
          <div className="text-sm font-medium text-accent-500 dark:text-accent-400">{title}</div>
          {icon && <div className="text-accent-400">{icon}</div>}
          <button className="text-accent-400 hover:text-accent-600 dark:hover:text-accent-300">
            <MoreVertical className="w-4 h-4" />
          </button>
        </div>
        
        <div className="flex items-end justify-between">
          <div>
            <div className="text-2xl font-bold text-accent-900 dark:text-accent-100">{value}</div>
            {trend !== undefined && (
              <div
                className={cn(
                  "flex items-center gap-1 text-xs mt-1",
                  isPositive
                    ? "text-success-600 dark:text-success-400"
                    : isNeutral
                    ? "text-accent-500 dark:text-accent-400"
                    : "text-error-600 dark:text-error-400"
                )}
              >
                {isPositive ? (
                  <TrendingUp className="w-3 h-3" />
                ) : (
                  <TrendingDown className="w-3 h-3" />
                )}
                <span className="font-medium">
                  {isPositive ? "+" : ""}
                  {trend}%
                </span>
                {trendLabel && (
                  <span className="text-accent-400 dark:text-accent-500 ml-1">{trendLabel}</span>
                )}
              </div>
            )}
          </div>
          
          {/* Sparkline */}
          {sparklineData && sparklineData.length > 0 && (
            <svg width="80" height="32" className="overflow-visible">
              <path
                d={generateSparkline(sparklineData)}
                fill="none"
                stroke={isPositive ? "#22c55e" : isNeutral ? "#64748b" : "#ef4444"}
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
          )}
        </div>
      </CardContent>
    </Card>
  )
}
