import { cn } from "@/lib/utils"

interface SkeletonProps {
  className?: string
  style?: React.CSSProperties
}

export function Skeleton({ className, style }: SkeletonProps) {
  return (
    <div
      className={cn(
        "animate-pulse rounded-md bg-accent-200 dark:bg-accent-700",
        className
      )}
      style={style}
    />
  )
}

export function TableRowSkeleton({ columns = 5 }: { columns?: number }) {
  return (
    <div className="flex items-center gap-4 p-4 border-b border-accent-200 dark:border-accent-700">
      <Skeleton className="w-4 h-4 rounded" />
      {Array.from({ length: columns }).map((_, i) => (
        <Skeleton key={i} className="flex-1 h-4" />
      ))}
    </div>
  )
}

export function TableSkeleton({ rows = 5, columns = 5 }: { rows?: number; columns?: number }) {
  return (
    <div className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 shadow-sm">
      <div className="p-4 border-b border-accent-200 dark:border-accent-800">
        <Skeleton className="h-6 w-32" />
      </div>
      <div className="p-4 space-y-3">
        {Array.from({ length: rows }).map((_, i) => (
          <TableRowSkeleton key={i} columns={columns} />
        ))}
      </div>
    </div>
  )
}

export function StatsCardSkeleton() {
  return (
    <div className="rounded-lg p-4 shadow-md bg-white dark:bg-accent-900 border border-accent-200 dark:border-accent-700">
      <Skeleton className="h-4 w-24 mb-2" />
      <Skeleton className="h-8 w-16" />
      <Skeleton className="h-4 w-20 mt-2" />
    </div>
  )
}

export function StatsGridSkeleton({ cards = 3 }: { cards?: number }) {
  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      {Array.from({ length: cards }).map((_, i) => (
        <StatsCardSkeleton key={i} />
      ))}
    </div>
  )
}

export function ChartSkeleton({ height = 300 }: { height?: number }) {
  return (
    <div className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 p-4">
      <Skeleton className="h-6 w-32 mb-4" />
      <Skeleton className="w-full" style={{ height }} />
    </div>
  )
}

export function FormFieldSkeleton() {
  return (
    <div className="space-y-2">
      <Skeleton className="h-4 w-24" />
      <Skeleton className="h-10 w-full" />
    </div>
  )
}

export function CardSkeleton() {
  return (
    <div className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 p-4">
      <Skeleton className="h-6 w-32 mb-4" />
      <Skeleton className="h-4 w-full mb-2" />
      <Skeleton className="h-4 w-3/4" />
    </div>
  )
}
