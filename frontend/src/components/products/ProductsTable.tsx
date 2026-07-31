import { useNavigate } from "react-router-dom"
import type { Product } from "@/types"
import { formatCurrency, formatNumber } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import { Badge } from "@/components/ui/badge"

interface ProductsTableProps {
  products: Product[]
  loading: boolean
  currentPage: number
  pageSize: number
  totalElements: number
  totalPages: number
  onPageChange: (page: number) => void
  onEdit: (product: Product) => void
  onDelete: (product: Product) => void
}

export default function ProductsTable({
  products,
  loading,
  currentPage,
  pageSize,
  totalElements,
  totalPages,
  onPageChange,
  onEdit,
  onDelete,
}: ProductsTableProps) {
  const navigate = useNavigate()

  const getStockStatusBadge = (product: Product) => {
    const status = product.stockStatus || "IN_STOCK"
    const variants = {
      IN_STOCK: { label: "In Stock", className: "bg-success-100 dark:bg-success-900/30 text-success-700 dark:text-success-400" },
      LOW_STOCK: { label: "Low Stock", className: "bg-warning-100 dark:bg-warning-900/30 text-warning-700 dark:text-warning-400" },
      OUT_OF_STOCK: { label: "Out of Stock", className: "bg-error-100 dark:bg-error-900/30 text-error-700 dark:text-error-400" },
    }
    const variant = variants[status] || variants.IN_STOCK
    return <Badge className={variant.className}>{variant.label}</Badge>
  }

  const getStockLevel = (product: Product) => {
    return product.totalStock || product.availableStock || 0
  }

  const getRecLevel = (product: Product) => {
    return product.reorderLevel || 0
  }

  const renderPagination = () => {
    const pages = []
    const maxVisiblePages = 5
    let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2))
    const endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1)

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

  if (products.length === 0) {
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
          <h3 className="text-lg font-medium text-accent-900 dark:text-accent-100 mb-1">No products found</h3>
          <p className="text-accent-500 dark:text-accent-400">Try adjusting your search or filters</p>
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
                Product Name
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Product ID
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Category
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Price
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
            {products.map((product, index) => (
              <tr
                key={product.id}
                className={`border-b border-accent-200 dark:border-accent-700 hover:bg-accent-50 dark:hover:bg-accent-800 transition-colors ${
                  index % 2 === 0 ? "bg-white dark:bg-accent-900" : "bg-accent-50/50 dark:bg-accent-800/50"
                }`}
              >
                <td className="px-4 py-3">
                  <Checkbox />
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-primary-100 dark:bg-primary-900/30 flex items-center justify-center text-primary-600 dark:text-primary-400 font-medium">
                      {product.imageUrl ? (
                        <img
                          src={product.imageUrl}
                          alt={product.name}
                          className="w-10 h-10 rounded-full object-cover"
                        />
                      ) : (
                        product.name.charAt(0).toUpperCase()
                      )}
                    </div>
                    <span className="font-medium text-accent-900 dark:text-accent-100">{product.name}</span>
                  </div>
                </td>
                <td className="px-4 py-3 text-sm text-accent-500 dark:text-accent-400">
                  {product.sku}
                </td>
                <td className="px-4 py-3">
                  {product.category ? (
                    <button
                      onClick={() => navigate(`/category?id=${product.category?.id}`)}
                      className="text-primary-500 hover:underline text-sm dark:text-primary-400"
                    >
                      {product.category.name}
                    </button>
                  ) : (
                    <span className="text-accent-400 dark:text-accent-500 text-sm">—</span>
                  )}
                </td>
                <td className="px-4 py-3 text-sm font-medium text-accent-900 dark:text-accent-100">
                  {formatCurrency(product.unitPrice)}
                </td>
                <td className="px-4 py-3 text-sm text-accent-500 dark:text-accent-400">
                  {product.costPrice ? `${product.costPrice} lbs` : "—"}
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-2">
                    <span className="text-sm font-medium text-accent-900 dark:text-accent-100">{formatNumber(getStockLevel(product))}</span>
                    {getStockStatusBadge(product)}
                  </div>
                </td>
                <td className="px-4 py-3 text-sm text-accent-500 dark:text-accent-400">
                  {formatNumber(getRecLevel(product))}
                </td>
                <td className="px-4 py-3">
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => onEdit(product)}
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
                      onClick={() => onDelete(product)}
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
