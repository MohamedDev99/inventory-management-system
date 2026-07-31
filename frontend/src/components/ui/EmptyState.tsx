import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"

interface EmptyStateProps {
  icon?: React.ReactNode
  title: string
  description?: string
  action?: {
    label: string
    onClick: () => void
  }
  className?: string
}

export default function EmptyState({
  icon,
  title,
  description,
  action,
  className,
}: EmptyStateProps) {
  return (
    <div
      className={cn(
        "flex flex-col items-center justify-center py-12 px-4",
        className
      )}
    >
      {icon && (
        <div className="mb-4 text-accent-400 dark:text-accent-600">
          {icon}
        </div>
      )}
      <h3 className="text-lg font-medium text-accent-900 dark:text-accent-100 mb-1">
        {title}
      </h3>
      {description && (
        <p className="text-accent-500 dark:text-accent-400 text-center max-w-sm mb-4">
          {description}
        </p>
      )}
      {action && (
        <Button onClick={action.onClick} className="bg-primary-500 hover:bg-primary-600">
          {action.label}
        </Button>
      )}
    </div>
  )
}
