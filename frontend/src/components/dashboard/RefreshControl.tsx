import { RefreshCw, Play, Pause } from "lucide-react"
import { Button } from "@/components/ui"
import { cn } from "@/lib/utils"

interface RefreshControlProps {
  isRefreshing: boolean
  isEnabled: boolean
  lastRefreshed: string
  onToggle: () => void
  onRefresh: () => void
}

export default function RefreshControl({
  isRefreshing,
  isEnabled,
  lastRefreshed,
  onToggle,
  onRefresh,
}: RefreshControlProps) {
  return (
    <div className="flex items-center gap-2">
      <div className="text-xs text-accent-500 dark:text-accent-400">
        {isRefreshing ? (
          <span className="flex items-center gap-1">
            <RefreshCw className="w-3 h-3 animate-spin" />
            Refreshing...
          </span>
        ) : (
          <span>Updated {lastRefreshed}</span>
        )}
      </div>
      
      <Button
        variant="ghost"
        size="sm"
        onClick={onRefresh}
        disabled={isRefreshing}
        className={cn(
          "h-7 px-2",
          isRefreshing && "opacity-50"
        )}
      >
        <RefreshCw className={cn("w-3 h-3", isRefreshing && "animate-spin")} />
      </Button>
      
      <Button
        variant={isEnabled ? "default" : "outline"}
        size="sm"
        onClick={onToggle}
        className={cn(
          "h-7 px-2",
          isEnabled ? "bg-primary-500 hover:bg-primary-600" : ""
        )}
      >
        {isEnabled ? (
          <Pause className="w-3 h-3" />
        ) : (
          <Play className="w-3 h-3" />
        )}
      </Button>
    </div>
  )
}
