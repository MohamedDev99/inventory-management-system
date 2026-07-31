import { useNavigate } from "react-router-dom"
import type { InventoryItem } from "@/types"
import { formatNumber, formatDateTime } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"

interface StockTableProps {
  stockItems: InventoryItem[]
  loading: boolean
  currentPage: number
  pageSize: number
  totalElements: number
  totalPages: number
  onPageChange: (page: number) => void
  onEdit: (stock: InventoryItem) => void
}

export default function StockTable({
  stockItems,
  loading,
  currentPage,
  pageSize,
  totalElements,
  totalPages,
  onPageChange,
  onEdit,
}: StockTableProps) {
  const navigate = useNavigate()

  const renderPagination = () => {
    const pages = []
    const maxVisiblePages = 5
    let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2))
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1)

    if (endPage - startPage < maxVisiblePages - 1) {
      startPage = Math.max(0, endPage - maxVisiblePages + 1)
    }

    for (let i = startPage; i <= endPage; i++) {
      pages.push(
        <button
          key={i}
          onClick={() => onPageChange(i)}
          className={`w-8 h-8 text-sm rounded-md transition-colors ${
            currentPage === i
              ? "bg-primary-500 text-white dark:bg-primary-500"
              : "text-accent-500 dark:text-accent-400 hover:bg-accent-100 dark:hover:bg-accent-800"
          }`}
        >
          {i + 1}
        </button>
      )
    }

    return (
      <div className="flex items-center gap-2">
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0}
        >
          Previous
        </Button>
        {startPage > 0 && (
          <>
            <button
              onClick={() => onPageChange(0)}
              className="w-8 h-8 text-sm text-accent-500 dark:text-accent-400 hover:text-accent-900 dark:hover:text-accent-100"
            >
              1
            </button>
            {startPage > 1 && <span className="text-accent-500 dark:text-accent-400">...</span>}
          </>
        )}
        {pages}
        {endPage < totalPages - 1 && (
          <>
            {endPage < totalPages - 2 && <span className="text-accent-500 dark:text-accent-400">...</span>}
            <button
              onClick={() => onPageChange(totalPages - 1)}
              className="w-8 h-8 text-sm text-accent-500 dark:text-accent-400 hover:text-accent-900 dark:hover:text-accent-100"
            >
              {totalPages}
            </button>
          </>
        )}
        <Button
          variant="outline"
          size="sm"
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage === totalPages - 1}
        >
          Next
        </Button>
      </div>
    )
  }

  if (loading) {
    return (
      <div className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 shadow-sm">
        <div className="p-4 border-b border-accent-200 dark:border-accent-800">
          <div className="h-6 bg-accent-200 dark:bg-accent-700 animate-pulse rounded w-32"></div>
        </div>
        <div className="p-4 space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-12 bg-accent-100 dark:bg-accent-800 animate-pulse rounded"></div>
          ))}
        </div>
      </div>
    )
  }

  if (stockItems.length === 0) {
    return (
      <div className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 shadow-sm">
        <div className="flex flex-col items-center justify-center py-12">
          <svg
            className="w-16 h-16 text-accent-400 dark:text-accent-600 mb-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={1.5}
              d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
            />
          </svg>
          <h3 className="text-lg font-medium text-accent-900 dark:text-accent-100 mb-1">No stock found</h3>
          <p className="text-accent-500 dark:text-accent-400">Try adjusting your search or filters</p>
        </div>
      </div>
    )
  }

  if (stockItems.length === 0) {
    return (
      <div className="bg-white rounded-lg border shadow-sm">
        <div className="flex flex-col items-center justify-center py-12">
          <svg
            className="w-16 h-16 text-muted-foreground mb-4"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={1.5}
              d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
            />
          </svg>
          <h3 className="text-lg font-medium text-gray-900 mb-1">No stock found</h3>
          <p className="text-muted-foreground">Try adjusting your search or filters</p>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 shadow-sm">
      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b bg-accent-50 dark:bg-accent-800 border-accent-200 dark:border-accent-700">
              <th className="px-4 py-3 text-left">
                <Checkbox />
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Created Date
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Stock ID
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Product ID
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Warehouse ID
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Category
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Weight
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Stock Level
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Rec. Level
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Actions
              </th>
            </tr>
          </thead>
          <tbody>
            {stockItems.map((stock, index) => (
              <tr
                key={stock.id}
                className={`border-b border-accent-200 dark:border-accent-700 hover:bg-accent-50 dark:hover:bg-accent-800 transition-colors ${
                  index % 2 === 0 ? "bg-white dark:bg-accent-900" : "bg-accent-50/50 dark:bg-accent-800/50"
                }`}
              >
                <td className="px-4 py-3">
                  <Checkbox />
                </td>
                <td className="px-4 py-3 text-sm text-accent-500 dark:text-accent-400">
                  {formatDateTime(stock.createdAt)}
                </td>
                <td className="px-4 py-3 text-sm font-medium text-accent-900 dark:text-accent-100">
                  #{stock.id}
                </td>
                <td className="px-4 py-3">
                  <button
                    onClick={() => navigate(`/products?id=${stock.product?.id}`)}
                    className="text-primary-500 hover:underline text-sm dark:text-primary-400"
                  >
                    {stock.product?.sku || "—"}
                  </button>
                </td>
                <td className="px-4 py-3">
                  <button
                    onClick={() => navigate(`/warehouse?id=${stock.warehouse?.id}`)}
                    className="text-primary-500 hover:underline text-sm dark:text-primary-400"
                  >
                    {stock.warehouse?.code || "—"}
                  </button>
                </td>
                <td className="px-4 py-3">
                  <button
                    onClick={() => navigate(`/category?id=${stock.product?.category?.id}`)}
                    className="text-primary-500 hover:underline text-sm dark:text-primary-400"
                  >
                    {stock.product?.category?.name || "—"}
                  </button>
                </td>
                <td className="px-4 py-3 text-sm text-accent-500 dark:text-accent-400">
                  {stock.product?.costPrice ? `${stock.product.costPrice} lbs` : "—"}
                </td>
                <td className="px-4 py-3 text-sm font-medium text-accent-900 dark:text-accent-100">
                  {formatNumber(stock.quantity)}
                </td>
                <td className="px-4 py-3 text-sm text-accent-500 dark:text-accent-400">
                  {stock.reorderLevel ? formatNumber(stock.reorderLevel) : "—"}
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => onEdit(stock)}
                      className="p-1.5 text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/30 rounded transition-colors"
                      title="Edit"
                    >
                      <svg
                        className="w-4 h-4"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"
                        />
                      </svg>
                    </button>
                    <button
                      className="p-1.5 text-accent-500 dark:text-accent-400 hover:bg-accent-100 dark:hover:bg-accent-800 rounded transition-colors"
                      title="View"
                    >
                      <svg
                        className="w-4 h-4"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                        />
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
                        />
                      </svg>
                    </button>
                    <button
                      className="p-1.5 text-error-500 hover:bg-error-50 dark:hover:bg-error-900/30 rounded transition-colors"
                      title="Delete"
                    >
                      <svg
                        className="w-4 h-4"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                        />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="px-4 py-3 border-t border-accent-200 dark:border-accent-700 flex items-center justify-between">
        <div className="text-sm text-accent-500 dark:text-accent-400">
          Showing {currentPage * pageSize + 1} to{" "}
          {Math.min((currentPage + 1) * pageSize, totalElements)} of{" "}
          {totalElements} results
        </div>
        {renderPagination()}
      </div>
    </div>
  )
}
