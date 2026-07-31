import * as React from "react"
import { cva, type VariantProps } from "class-variance-authority"
import { cn } from "@/lib/utils"

const badgeVariants = cva(
  "inline-flex items-center rounded-full border px-2.5 py-0.5 text-xs font-semibold transition-colors focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2",
  {
    variants: {
      variant: {
        default:
          "border-transparent bg-primary-500 text-white hover:bg-primary-600 dark:bg-primary-500 dark:text-white",
        secondary:
          "border-transparent bg-accent-100 text-accent-900 hover:bg-accent-200 dark:bg-accent-700 dark:text-accent-100 dark:hover:bg-accent-600",
        destructive:
          "border-transparent bg-error-500 text-white hover:bg-error-600 dark:bg-error-500 dark:text-white",
        outline: "text-accent-900 dark:text-accent-100 border-accent-200 dark:border-accent-700",
        success:
          "border-transparent bg-success-500 text-white hover:bg-success-600 dark:bg-success-500 dark:text-white",
        warning:
          "border-transparent bg-warning-500 text-white hover:bg-warning-600 dark:bg-warning-500 dark:text-white",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
)

export interface BadgeProps
  extends React.HTMLAttributes<HTMLDivElement>,
    VariantProps<typeof badgeVariants> {}

function Badge({ className, variant, ...props }: BadgeProps) {
  return (
    <div className={cn(badgeVariants({ variant }), className)} {...props} />
  )
}

export { Badge, badgeVariants }
