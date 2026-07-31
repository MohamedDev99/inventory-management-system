import { useState } from "react"
import { useNavigate } from "react-router-dom"
import type { SalesOrder, SalesOrderStatus } from "@/types"
import { formatDateTime } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"

interface SalesOrdersTableProps {
  orders: SalesOrder[]
  loading: boolean
  currentPage: number
  pageSize: number
  totalElements: number
  totalPages: number
  onPageChange: (page: number) => void
  onConfirm: (id: number) => void
  onFulfill: (id: number) => void
  onShip: (id: number) => void
  onDeliver: (id: number) => void
  onCancel: (id: number, reason: string) => void
  onDelete: (id: number) => void
}

const statusConfig: Record<SalesOrderStatus, { label: string; className: string }> = {
  PENDING: { label: "Pending", className: "bg-warning-100 dark:bg-warning-900/30 text-warning-700 dark:text-warning-400" },
  CONFIRMED: { label: "Confirmed", className: "bg-primary-100 dark:bg-primary-900/30 text-primary-700 dark:text-primary-400" },
  PROCESSING: { label: "Processing", className: "bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400" },
  SHIPPED: { label: "In Transit", className: "bg-primary-100 dark:bg-primary-900/30 text-primary-700 dark:text-primary-400" },
  DELIVERED: { label: "Delivered", className: "bg-success-100 dark:bg-success-900/30 text-success-700 dark:text-success-400" },
  CANCELLED: { label: "Cancelled", className: "bg-accent-100 dark:bg-accent-700 text-accent-700 dark:text-accent-200" },
  FULFILLED: { label: "Fulfilled", className: "bg-success-100 dark:bg-success-900/30 text-success-700 dark:text-success-400" }
}

export default function SalesOrdersTable({
  orders,
  loading,
  currentPage,
  pageSize,
  totalElements,
  totalPages,
  onPageChange,
  onConfirm,
  onFulfill,
  onShip,
  onDeliver,
  onCancel,
  onDelete,
}: SalesOrdersTableProps) {
  const handleCancel = (id: number) => {
    const reason = window.prompt("Please enter a reason for cancellation:")
    if (reason) {
      onCancel(id, reason)
    }
  }
  const navigate = useNavigate()
  const [hoveredOrderId, setHoveredOrderId] = useState<number | null>(null)

  const getStatusBadge = (status: SalesOrderStatus) => {
    const config = statusConfig[status] || statusConfig.PENDING
    return (
      <span className={`px-2 py-1 text-xs font-medium rounded-full ${config.className}`}>
        {config.label}
      </span>
    )
  }

  // Render context-sensitive actions based on status
  const renderActions = (order: SalesOrder) => {
    const status = order.status

    switch (status) {
      case "PENDING":
        return (
          <div className="flex items-center gap-2">
            <button
              onClick={() => onConfirm(order.id)}
              className="p-1.5 text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/30 rounded transition-colors"
              title="Confirm"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </button>
            <button
              onClick={() => onDelete(order.id)}
              className="p-1.5 text-error-500 hover:bg-error-50 dark:hover:bg-error-900/30 rounded transition-colors"
              title="Delete"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
        )
      case "CONFIRMED":
        return (
          <div className="flex items-center gap-2">
            <button
              onClick={() => onFulfill(order.id)}
              className="p-1.5 text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/30 rounded transition-colors"
              title="Fulfill"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
              </svg>
            </button>
            <button
              onClick={() => handleCancel(order.id)}
              className="p-1.5 text-error-500 hover:bg-error-50 dark:hover:bg-error-900/30 rounded transition-colors"
              title="Cancel"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        )
      case "PROCESSING":
        return (
          <div className="flex items-center gap-2">
            <button
              onClick={() => onShip(order.id)}
              className="p-1.5 text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/30 rounded transition-colors"
              title="Ship"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
              </svg>
            </button>
            <button
              onClick={() => handleCancel(order.id)}
              className="p-1.5 text-error-500 hover:bg-error-50 dark:hover:bg-error-900/30 rounded transition-colors"
              title="Cancel"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        )
      case "SHIPPED":
        return (
          <div className="flex items-center gap-2">
            <button
              onClick={() => onDeliver(order.id)}
              className="p-1.5 text-success-500 hover:bg-success-50 dark:hover:bg-success-900/30 rounded transition-colors"
              title="Mark Delivered"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </button>
          </div>
        )
      case "DELIVERED":
        return (
          <div className="flex items-center gap-2">
            <button
              className="p-1.5 text-accent-500 dark:text-accent-400 hover:bg-accent-100 dark:hover:bg-accent-800 rounded transition-colors"
              title="View Receipt"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </button>
            <button
              className="p-1.5 text-success-500 hover:bg-success-50 dark:hover:bg-success-900/30 rounded transition-colors"
              title="Download Invoice"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
              </svg>
            </button>
          </div>
        )
      case "CANCELLED":
        return (
          <div className="flex items-center gap-2">
            <button
              onClick={() => onDelete(order.id)}
              className="p-1.5 text-error-500 hover:bg-error-50 dark:hover:bg-error-900/30 rounded transition-colors"
              title="Delete"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
              </svg>
            </button>
          </div>
        )
      default:
          return (
            <div className="flex items-center gap-2">
              <button
                className="p-1.5 text-accent-500 dark:text-accent-400 hover:bg-accent-100 dark:hover:bg-accent-800 rounded transition-colors"
                title="View"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              </button>
            </div>
          )
      // default:
      //   return (
      //     <div className="flex items-center gap-2">
      //       <button
      //         className="p-1.5 text-accent-500 dark:text-accent-400 hover:bg-accent-100 dark:hover:bg-accent-800 rounded transition-colors"
      //         title="View Receipt"
      //       >
      //         <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      //           <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
      //         </svg>
      //       </button>
      //       <button
      //         className="p-1.5 text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/30 rounded transition-colors"
      //         title="Track"
      //       >
      //         <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      //           <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
      //           <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
      //         </svg>
      //       </button>
      //     </div>
      //   )
    
    }
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

  if (orders.length === 0) {
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
              d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
            />
          </svg>
          <h3 className="text-lg font-medium text-accent-900 dark:text-accent-100 mb-1">No sales orders found</h3>
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
                Ordered Date
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Sales Order ID
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Customer ID
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                ETD
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Status
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-accent-500 dark:text-accent-400">
                Actions
              </th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order, index) => (
              <tr
                key={order.id}
                className={`border-b border-accent-200 dark:border-accent-700 hover:bg-accent-50 dark:hover:bg-accent-800 transition-colors ${
                  index % 2 === 0 ? "bg-white dark:bg-accent-900" : "bg-accent-50/50 dark:bg-accent-800/50"
                }`}
                onMouseEnter={() => setHoveredOrderId(order.id)}
                onMouseLeave={() => setHoveredOrderId(null)}
              >
                <td className="px-4 py-3">
                  <Checkbox />
                </td>
                <td className="px-4 py-3 text-sm text-accent-500 dark:text-accent-400">
                  {formatDateTime(order.orderDate)}
                </td>
                <td className="px-4 py-3">
                  <div className="relative">
                    <span className="text-sm font-medium text-accent-900 dark:text-accent-100 cursor-help">
                      {order.soNumber}
                    </span>
                    {/* Remarks Tooltip */}
                    {hoveredOrderId === order.id && order.notes && (
                      <div className="absolute z-10 bottom-full left-0 mb-2 w-64 p-2 bg-accent-800 dark:bg-accent-950 text-accent-100 dark:text-accent-100 text-xs rounded shadow-lg border border-accent-700">
                        {order.notes}
                      </div>
                    )}
                  </div>
                </td>
                <td className="px-4 py-3">
                  <button
                    onClick={() => navigate(`/customer?id=${order.customer?.id}`)}
                    className="text-primary-500 hover:underline text-sm dark:text-primary-400"
                  >
                    {order.customer?.customerCode || "—"}
                  </button>
                </td>
                <td className="px-4 py-3 text-sm text-accent-500 dark:text-accent-400">
                  {order.deliveryDate ? formatDateTime(order.deliveryDate) : "NA"}
                </td>
                <td className="px-4 py-3">
                  {getStatusBadge(order.status)}
                </td>
                <td className="px-4 py-3">
                  {renderActions(order)}
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
