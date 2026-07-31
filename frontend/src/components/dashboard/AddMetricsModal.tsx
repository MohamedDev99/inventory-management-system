import { Check } from "lucide-react"
import { Button } from "@/components/ui"
import { cn } from "@/lib/utils"
import type { KPIConfig } from "@/hooks/useKPIVisibility"

interface AddMetricsModalProps {
  isOpen: boolean
  onClose: () => void
  kpis: KPIConfig[]
  onToggle: (id: string) => void
  onReset: () => void
}

export default function AddMetricsModal({
  isOpen,
  onClose,
  kpis,
  onToggle,
  onReset,
}: AddMetricsModalProps) {
  if (!isOpen) return null

  const hasVisibleKPIs = kpis.some((kpi) => kpi.visible)

  return (
    <>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/50 z-40"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div
          className="bg-white dark:bg-accent-900 rounded-lg shadow-xl w-full max-w-md animate-slide-up"
          onClick={(e) => e.stopPropagation()}
        >
          {/* Header */}
          <div className="flex items-center justify-between p-4 border-b border-accent-200 dark:border-accent-700">
            <h2 className="text-lg font-semibold text-accent-900 dark:text-accent-100">
              Customize Dashboard Metrics
            </h2>
            <button
              onClick={onClose}
              className="text-accent-400 hover:text-accent-600 dark:hover:text-accent-200"
            >
              <span className="text-2xl">&times;</span>
            </button>
          </div>

          {/* Content */}
          <div className="p-4">
            <p className="text-sm text-accent-500 dark:text-accent-400 mb-4">
              Select which KPI cards you want to display on your dashboard.
            </p>

            <div className="space-y-2">
              {kpis.map((kpi) => (
                <button
                  key={kpi.id}
                  onClick={() => onToggle(kpi.id)}
                  className={cn(
                    "w-full flex items-center justify-between p-3 rounded-lg border transition-all duration-200",
                    kpi.visible
                      ? "border-primary-500 bg-primary-50 dark:bg-primary-950"
                      : "border-accent-200 dark:border-accent-700 hover:border-accent-300 dark:hover:border-accent-600"
                  )}
                >
                  <span className="text-sm font-medium text-accent-900 dark:text-accent-100">
                    {kpi.title}
                  </span>
                  <div
                    className={cn(
                      "w-5 h-5 rounded flex items-center justify-center transition-colors",
                      kpi.visible
                        ? "bg-primary-500 text-white"
                        : "bg-accent-200 dark:bg-accent-700"
                    )}
                  >
                    {kpi.visible && <Check className="w-3 h-3" />}
                  </div>
                </button>
              ))}
            </div>
          </div>

          {/* Footer */}
          <div className="flex items-center justify-between p-4 border-t border-accent-200 dark:border-accent-700">
            <Button
              variant="ghost"
              size="sm"
              onClick={onReset}
              disabled={!hasVisibleKPIs}
              className="text-accent-500 hover:text-accent-700 dark:text-accent-400 dark:hover:text-accent-200"
            >
              Reset to Default
            </Button>
            <Button
              onClick={onClose}
              className="bg-primary-500 hover:bg-primary-600 text-white"
            >
              Done
            </Button>
          </div>
        </div>
      </div>
    </>
  )
}
