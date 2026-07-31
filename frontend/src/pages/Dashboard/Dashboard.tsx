import { Calendar, Filter, Plus, ChevronDown, ChevronUp, X } from "lucide-react"
import { Button } from "@/components/ui"
import { Skeleton } from "@/components/ui/skeleton"
import KPICard from "@/components/dashboard/KPICard"
import SalesChart from "@/components/dashboard/SalesChart"
import CategoryDonutChart from "@/components/dashboard/CategoryDonutChart"
import ProductsBarChart from "@/components/dashboard/ProductsBarChart"
import AddMetricsModal from "@/components/dashboard/AddMetricsModal"
import DateRangePicker from "@/components/dashboard/DateRangePicker"
import RefreshControl from "@/components/dashboard/RefreshControl"
import TimeFilterBar from "@/components/ui/TimeFilterBar"
import { useDashboard } from "./useDashboard"

export default function Dashboard() {
  const {
    user,
    selectedRange,
    setSelectedRange,
    salesChartCollapsed,
    setSalesChartCollapsed,
    isMetricsModalOpen,
    setIsMetricsModalOpen,
    isDatePickerOpen,
    setIsDatePickerOpen,
    kpis,
    visibleKPIs,
    toggleKPI,
    resetToDefault,
    dateRange,
    updateDateRange,
    clearDateRange,
    hasDateRange,
    formatDateRange,
    isRefreshing,
    autoRefreshEnabled,
    toggleEnabled,
    triggerRefresh,
    formatLastRefreshed,
    overviewData,
    salesAnalyticsData,
    topProductsData,
    inventorySummaryData,
    salesLoading,
    topProductsLoading,
    inventorySummaryLoading,
    displayKPIs,
    isLoading,
  } = useDashboard()

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-accent-900 dark:text-accent-100">
            Welcome back, {user?.username || "User"}!
          </h1>
          <p className="text-accent-500 dark:text-accent-400">Here's what's happening with your business today.</p>
        </div>
        
        <div className="flex items-center gap-2">
          {/* Auto-refresh Control */}
          <RefreshControl
            isRefreshing={isRefreshing}
            isEnabled={autoRefreshEnabled}
            lastRefreshed={formatLastRefreshed()}
            onToggle={toggleEnabled}
            onRefresh={triggerRefresh}
          />
          
          {/* Time Filter */}
          <TimeFilterBar
            value={selectedRange}
            onChange={setSelectedRange}
            showSelectDates={false}
          />
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex items-center gap-2">
        <Button variant="outline" size="sm" onClick={() => setIsMetricsModalOpen(true)}>
          <Plus className="w-4 h-4 mr-1" />
          Add Metrics
        </Button>
        <Button 
          variant={hasDateRange ? "default" : "outline"} 
          size="sm" 
          onClick={() => setIsDatePickerOpen(true)}
          className={hasDateRange ? "bg-primary-500 hover:bg-primary-600" : ""}
        >
          <Calendar className="w-4 h-4 mr-1" />
          {hasDateRange ? formatDateRange() : "Select Dates"}
          {hasDateRange && (
            <X 
              className="w-3 h-3 ml-1" 
              onClick={(e) => {
                e.stopPropagation()
                clearDateRange()
              }}
            />
          )}
        </Button>
        <Button variant="outline" size="sm">
          <Filter className="w-4 h-4 mr-1" />
          Filters
        </Button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {isLoading
          ? Array.from({ length: displayKPIs.length || 6 }).map((_, i) => (
              <div key={i} className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 p-4">
                <Skeleton className="h-4 w-1/2 mb-4" />
                <Skeleton className="h-8 w-3/4" />
              </div>
            ))
          : displayKPIs.map((kpi) => (
              <KPICard
                key={kpi.id}
                title={kpi.title}
                value={kpi.value}
                trend={kpi.trend}
                trendLabel={kpi.trendLabel}
                sparklineData={kpi.sparklineData}
              />
            ))}
      </div>

      {/* Charts Row 1 */}
      <div className="relative">
        <SalesChart 
          collapsed={salesChartCollapsed} 
          data={salesAnalyticsData?.data as unknown[] || []}
          loading={salesLoading}
        />
        
        {/* Collapse Button */}
        <button
          onClick={() => setSalesChartCollapsed(!salesChartCollapsed)}
          className="absolute top-4 right-4 p-1 bg-accent-100 dark:bg-accent-700 rounded hover:bg-accent-200 dark:hover:bg-accent-600 transition-colors"
        >
          {salesChartCollapsed ? (
            <ChevronDown className="w-4 h-4 text-accent-600 dark:text-accent-300" />
          ) : (
            <ChevronUp className="w-4 h-4 text-accent-600 dark:text-accent-300" />
          )}
        </button>
      </div>

      {/* Charts Row 2 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="relative">
          <CategoryDonutChart 
            data={inventorySummaryData?.data as unknown[] || []}
            loading={inventorySummaryLoading}
          />
        </div>
        <div>
          <ProductsBarChart 
            data={topProductsData?.data as unknown[] || []}
            loading={topProductsLoading}
          />
        </div>
      </div>

      {/* Add Metrics Modal */}
      <AddMetricsModal
        isOpen={isMetricsModalOpen}
        onClose={() => setIsMetricsModalOpen(false)}
        kpis={kpis}
        onToggle={toggleKPI}
        onReset={resetToDefault}
      />

      {/* Date Range Picker */}
      <DateRangePicker
        isOpen={isDatePickerOpen}
        onClose={() => setIsDatePickerOpen(false)}
        onApply={updateDateRange}
        initialRange={dateRange}
      />
    </div>
  )
}
