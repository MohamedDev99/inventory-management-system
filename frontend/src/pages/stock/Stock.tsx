import { useNavigate } from "react-router-dom"
import StockTable from "@/components/stock/StockTable"
import AddStockModal from "@/components/stock/AddStockModal"
import EditStockModal from "@/components/stock/EditStockModal"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useStock } from "./useStock"

type TimeFilter = "1d" | "7d" | "1m" | "3m" | "6m" | "1y" | "3y" | "5y"

const timeFilters: TimeFilter[] = ["1d", "7d", "1m", "3m", "6m", "1y", "3y", "5y"]

export default function Stock() {
  const navigate = useNavigate()
  const {
    timeFilter,
    setTimeFilter,
    searchQuery,
    setSearchQuery,
    currentPage,
    setCurrentPage,
    pageSize,
    isAddModalOpen,
    setIsAddModalOpen,
    isEditModalOpen,
    setIsEditModalOpen,
    selectedStock,
    stockItems,
    totalElements,
    totalPages,
    isLoading,
    handleSearch,
    handleAddStock,
    handleEditStock,
    openEditModal,
    isAdding,
    isEditing,
  } = useStock()

  const handlePageChange = (page: number) => setCurrentPage(page)

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-2 text-sm text-accent-500 dark:text-accent-400">
        <button onClick={() => navigate(-1)} className="hover:text-primary-500 dark:hover:text-primary-400">
          ← Back
        </button>
        <span>/ Stock</span>
      </div>

      {/* Page Title */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-accent-900 dark:text-accent-100">Stock</h1>
      </div>

      {/* Time Filter Bar */}
      <div className="flex items-center gap-1 bg-gray-100 dark:bg-accent-800 p-1 rounded-lg w-fit">
        {timeFilters.map((filter) => (
          <button
            key={filter}
            onClick={() => setTimeFilter(filter)}
            className={`px-3 py-1.5 text-sm rounded-md transition-colors ${
              timeFilter === filter
                ? "bg-white dark:bg-accent-700 text-primary-500 dark:text-primary-400 shadow-sm font-medium"
                : "text-accent-500 dark:text-accent-400 hover:text-accent-900 dark:hover:text-accent-100"
            }`}
          >
            {filter}
          </button>
        ))}
        <button className="px-3 py-1.5 text-sm text-accent-500 dark:text-accent-400 hover:text-accent-900 dark:hover:text-accent-100">
          Select dates
        </button>
      </div>

      {/* Toolbar */}
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <div className="flex items-center gap-3">
          {/* Search */}
          <div className="relative">
            <Input
              type="text"
              placeholder="Search stock..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleSearch()}
              className="w-64 pl-10 bg-white dark:bg-accent-800 border-accent-200 dark:border-accent-700 text-accent-900 dark:text-accent-100"
            />
            <svg
              className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-accent-500 dark:text-accent-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
          </div>

          {/* Filters Button */}
          <Button variant="outline" size="sm">
            <svg
              className="w-4 h-4 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"
              />
            </svg>
            Filters
          </Button>
        </div>

        <div className="flex items-center gap-3">
          {/* Export Button */}
          <Button variant="outline" size="sm">
            <svg
              className="w-4 h-4 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12"
              />
            </svg>
            Export
          </Button>

          {/* Add New Stock Button */}
          <Button
            size="sm"
            className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400"
            onClick={() => setIsAddModalOpen(true)}
          >
            <svg
              className="w-4 h-4 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 4v16m8-8H4"
              />
            </svg>
            Add New Stock
          </Button>
        </div>
      </div>

      {/* Stock Table */}
      <StockTable
        stockItems={stockItems}
        loading={isLoading}
        currentPage={currentPage}
        pageSize={pageSize}
        totalElements={totalElements}
        totalPages={totalPages}
        onPageChange={handlePageChange}
        onEdit={openEditModal}
      />

      {/* Add Stock Modal */}
      <AddStockModal
        open={isAddModalOpen}
        onOpenChange={setIsAddModalOpen}
        onSubmit={handleAddStock}
        isLoading={isAdding}
      />

      {/* Edit Stock Modal */}
      <EditStockModal
        open={isEditModalOpen}
        onOpenChange={setIsEditModalOpen}
        stock={selectedStock}
        onSubmit={handleEditStock}
        isLoading={isEditing}
      />
    </div>
  )
}
