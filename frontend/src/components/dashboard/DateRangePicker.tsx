import { useState } from "react"
import { X } from "lucide-react"
import { Button } from "@/components/ui"

interface DateRange {
  startDate: Date | null
  endDate: Date | null
}

interface DateRangePickerProps {
  isOpen: boolean
  onClose: () => void
  onApply: (range: DateRange) => void
  initialRange?: DateRange
}

export default function DateRangePicker({
  isOpen,
  onClose,
  onApply,
  initialRange,
}: DateRangePickerProps) {
  const [selectedRange, setSelectedRange] = useState<DateRange>(
    initialRange || { startDate: null, endDate: null }
  )
  const [activePreset, setActivePreset] = useState<string | null>(null)

  const presets = [
    { label: "Today", days: 0 },
    { label: "Yesterday", days: -1 },
    { label: "Last 7 days", days: -7 },
    { label: "Last 30 days", days: -30 },
    { label: "This month", days: "thisMonth" },
    { label: "Last month", days: "lastMonth" },
  ]

  const handlePresetClick = (preset: typeof presets[0]) => {
    const today = new Date()
    let startDate: Date
    let endDate: Date = today

    if (preset.days === "thisMonth") {
      startDate = new Date(today.getFullYear(), today.getMonth(), 1)
      endDate = today
    } else if (preset.days === "lastMonth") {
      startDate = new Date(today.getFullYear(), today.getMonth() - 1, 1)
      endDate = new Date(today.getFullYear(), today.getMonth(), 0)
    } else {
      startDate = new Date(today)
      startDate.setDate(today.getDate() + (preset.days as number))
    }

    setSelectedRange({ startDate, endDate })
    setActivePreset(preset.label)
  }

  const handleStartDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const date = e.target.value ? new Date(e.target.value) : null
    setSelectedRange((prev) => ({ ...prev, startDate: date }))
    setActivePreset(null)
  }

  const handleEndDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const date = e.target.value ? new Date(e.target.value) : null
    setSelectedRange((prev) => ({ ...prev, endDate: date }))
    setActivePreset(null)
  }

  const handleApply = () => {
    onApply(selectedRange)
    onClose()
  }

  const handleClear = () => {
    setSelectedRange({ startDate: null, endDate: null })
    setActivePreset(null)
  }

  const formatDate = (date: Date | null): string => {
    if (!date) return ""
    return date.toISOString().split("T")[0]
  }

  if (!isOpen) return null

  return (
    <>
      {/* Backdrop */}
      <div className="fixed inset-0 bg-black/50 z-40" onClick={onClose} />

      {/* Modal */}
      <div className="fixed top-20 left-1/2 -translate-x-1/2 z-50 w-full max-w-sm">
        <div className="bg-white dark:bg-accent-900 rounded-lg shadow-xl border border-accent-200 dark:border-accent-700 animate-slide-up">
          {/* Header */}
          <div className="flex items-center justify-between p-3 border-b border-accent-200 dark:border-accent-700">
            <h3 className="text-sm font-semibold text-accent-900 dark:text-accent-100">
              Select Date Range
            </h3>
            <button
              onClick={onClose}
              className="text-accent-400 hover:text-accent-600 dark:hover:text-accent-200"
            >
              <X className="w-4 h-4" />
            </button>
          </div>

          {/* Presets */}
          <div className="p-3 border-b border-accent-200 dark:border-accent-700">
            <div className="grid grid-cols-3 gap-2">
              {presets.map((preset) => (
                <button
                  key={preset.label}
                  onClick={() => handlePresetClick(preset)}
                  className={`px-2 py-1.5 text-xs font-medium rounded-md transition-colors ${
                    activePreset === preset.label
                      ? "bg-primary-500 text-white"
                      : "bg-accent-100 dark:bg-accent-800 text-accent-700 dark:text-accent-300 hover:bg-accent-200 dark:hover:bg-accent-700"
                  }`}
                >
                  {preset.label}
                </button>
              ))}
            </div>
          </div>

          {/* Custom Date Inputs */}
          <div className="p-3 space-y-3">
            <div className="flex items-center gap-2">
              <label className="text-xs font-medium text-accent-600 dark:text-accent-400 w-16">
                From
              </label>
              <input
                type="date"
                value={formatDate(selectedRange.startDate)}
                onChange={handleStartDateChange}
                className="flex-1 px-3 py-1.5 text-sm border border-accent-200 dark:border-accent-700 rounded-md bg-white dark:bg-accent-800 text-accent-900 dark:text-accent-100 focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
            <div className="flex items-center gap-2">
              <label className="text-xs font-medium text-accent-600 dark:text-accent-400 w-16">
                To
              </label>
              <input
                type="date"
                value={formatDate(selectedRange.endDate)}
                onChange={handleEndDateChange}
                className="flex-1 px-3 py-1.5 text-sm border border-accent-200 dark:border-accent-700 rounded-md bg-white dark:bg-accent-800 text-accent-900 dark:text-accent-100 focus:outline-none focus:ring-2 focus:ring-primary-500"
              />
            </div>
          </div>

          {/* Footer */}
          <div className="flex items-center justify-between p-3 border-t border-accent-200 dark:border-accent-700">
            <Button
              variant="ghost"
              size="sm"
              onClick={handleClear}
              className="text-accent-500 hover:text-accent-700 dark:text-accent-400"
            >
              Clear
            </Button>
            <div className="flex items-center gap-2">
              <Button
                variant="outline"
                size="sm"
                onClick={onClose}
              >
                Cancel
              </Button>
              <Button
                size="sm"
                onClick={handleApply}
                className="bg-primary-500 hover:bg-primary-600 text-white"
              >
                Apply
              </Button>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}
