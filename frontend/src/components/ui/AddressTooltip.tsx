import { useState } from "react"
import { cn } from "@/lib/utils"

interface AddressTooltipProps {
  address: string
  maxLength?: number
  className?: string
}

export default function AddressTooltip({
  address,
  maxLength = 30,
  className,
}: AddressTooltipProps) {
  const [isHovered, setIsHovered] = useState(false)

  if (!address) return <span className="text-accent-500">—</span>

  const isTruncated = address.length > maxLength
  const displayText = isTruncated ? `${address.slice(0, maxLength)}...` : address

  return (
    <div 
      className={cn("relative inline-block", className)}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <span className="text-sm text-accent-500 dark:text-accent-400 cursor-help">
        {displayText}
      </span>
      
      {isHovered && isTruncated && (
        <div className="absolute z-10 bottom-full left-0 mb-2 w-64 p-2 bg-accent-800 dark:bg-accent-950 text-accent-100 dark:text-accent-100 text-xs rounded shadow-lg border border-accent-700">
          {address}
        </div>
      )}
    </div>
  )
}
