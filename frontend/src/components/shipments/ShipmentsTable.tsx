import { useNavigate } from "react-router-dom"
import type { Shipment, ShipmentStatus } from "@/types"
import { formatDateTime } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { Checkbox } from "@/components/ui/checkbox"
import type { UpdateShipmentStatusData } from "@/services/shipment/useUpdateShipmentStatus"
import type { DeliverShipmentData } from "@/services/shipment/useDeliverShipment"

interface ShipmentsTableProps {
  shipments: Shipment[]
  loading: boolean
  currentPage: number
  pageSize: number
  totalElements: number
  totalPages: number
  onPageChange: (page: number) => void
  onUpdateStatus: (id: number, data: UpdateShipmentStatusData) => void
  onDeliver: (id: number, data?: DeliverShipmentData) => void
}

const statusConfig: Record<ShipmentStatus, { label: string; className: string }> = {
  PENDING: { label: "Pending", className: "bg-gray-100 dark:bg-gray-800 text-gray-800 dark:text-gray-200" },
  IN_TRANSIT: { label: "In Transit", className: "bg-primary-100 dark:bg-primary-900/30 text-primary-800 dark:text-primary-300" },
  DELIVERED: { label: "Delivered", className: "bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-300" },
  DELAYED: { label: "Delayed", className: "bg-orange-100 dark:bg-orange-900/30 text-orange-800 dark:text-orange-300" },
  RTO: { label: "RTO", className: "bg-red-100 dark:bg-red-900/30 text-red-800 dark:text-red-300" },
  RE_ATTEMPT: { label: "Re-attempt", className: "bg-orange-200 dark:bg-orange-900/40 text-orange-900 dark:text-orange-200" },
}

export default function ShipmentsTable({
  shipments,
  loading,
  currentPage,
  pageSize,
  totalElements,
  totalPages,
  onPageChange,
  onUpdateStatus,
  onDeliver,
}: ShipmentsTableProps) {
  const navigate = useNavigate()

  const getStatusBadge = (status: ShipmentStatus) => {
    const config = statusConfig[status] || statusConfig.PENDING
    return (
      <span className={`px-2 py-1 text-xs font-medium rounded-full ${config.className}`}>
        {config.label}
      </span>
    )
  }

  // Render context-sensitive actions based on status
  const renderActions = (shipment: Shipment) => {
    const status = shipment.status

    switch (status) {
      case "DELAYED":
        return (
          <div className="flex items-center gap-2">
            <button
              className="p-1.5 text-muted-foreground hover:bg-gray-100 dark:hover:bg-gray-800 rounded transition-colors"
              title="View Receipt"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </button>
            <button
              onClick={() => onUpdateStatus(shipment.id, { status: "IN_TRANSIT" })}
              className="p-1.5 text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded transition-colors"
              title="Retry Delivery"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
            </button>
          </div>
        )
      case "DELIVERED":
        return (
          <div className="flex items-center gap-2">
            <button
              className="p-1.5 text-muted-foreground hover:bg-gray-100 dark:hover:bg-gray-800 rounded transition-colors"
              title="View Receipt"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </button>
          </div>
        )
      case "RTO":
        return (
          <div className="flex items-center gap-2">
            <button
              className="p-1.5 text-muted-foreground hover:bg-gray-100 dark:hover:bg-gray-800 rounded transition-colors"
              title="View Receipt"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </button>
            <button
              onClick={() => onUpdateStatus(shipment.id, { status: "RE_ATTEMPT" })}
              className="p-1.5 text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded transition-colors"
              title="Retry"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
            </button>
          </div>
        )
      case "IN_TRANSIT":
        return (
          <div className="flex items-center gap-2">
            <button
              className="p-1.5 text-muted-foreground hover:bg-gray-100 dark:hover:bg-gray-800 rounded transition-colors"
              title="View Receipt"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </button>
            <button
              onClick={() => onDeliver(shipment.id)}
              className="p-1.5 text-green-500 hover:bg-green-50 dark:hover:bg-green-900/20 rounded transition-colors"
              title="Mark Delivered"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </button>
          </div>
        )
      case "RE_ATTEMPT":
        return (
          <div className="flex items-center gap-2">
            <button
              onClick={() => onUpdateStatus(shipment.id, { status: "IN_TRANSIT" })}
              className="p-1.5 text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded transition-colors"
              title="Ship"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
              </svg>
            </button>
            <button
              onClick={() => onDeliver(shipment.id)}
              className="p-1.5 text-green-500 hover:bg-green-50 dark:hover:bg-green-900/20 rounded transition-colors"
              title="Mark Delivered"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </button>
          </div>
        )
      default:
        return (
          <div className="flex items-center gap-2">
            <button
              onClick={() => onUpdateStatus(shipment.id, { status: "IN_TRANSIT" })}
              className="p-1.5 text-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20 rounded transition-colors"
              title="Ship"
            >
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
              </svg>
            </button>
          </div>
        )
    }
  }

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
              ? "bg-primary-500 text-white"
              : "text-muted-foreground hover:bg-gray-100 dark:hover:bg-gray-800"
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
              className="w-8 h-8 text-sm text-muted-foreground hover:text-gray-900 dark:hover:text-gray-100"
            >
              1
            </button>
            {startPage > 1 && <span className="text-muted-foreground">...</span>}
          </>
        )}
        {pages}
        {endPage < totalPages - 1 && (
          <>
            {endPage < totalPages - 2 && <span className="text-muted-foreground">...</span>}
            <button
              onClick={() => onPageChange(totalPages - 1)}
              className="w-8 h-8 text-sm text-muted-foreground hover:text-gray-900 dark:hover:text-gray-100"
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
      <div className="bg-white dark:bg-accent-content rounded-lg border shadow-sm">
        <div className="p-4 border-b">
          <div className="h-6 bg-gray-200 dark:bg-gray-700 animate-pulse rounded w-32"></div>
        </div>
        <div className="p-4 space-y-4">
          {[...Array(5)].map((_, i) => (
            <div key={i} className="h-12 bg-gray-100 dark:bg-gray-800 animate-pulse rounded"></div>
          ))}
        </div>
      </div>
    )
  }

  if (shipments.length === 0) {
    return (
      <div className="bg-white dark:bg-accent-content rounded-lg border shadow-sm">
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
              d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4"
            />
          </svg>
          <h3 className="text-lg font-medium text-gray-900 dark:text-gray-100 mb-1">No shipments found</h3>
          <p className="text-muted-foreground">Try adjusting your search or filters</p>
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white dark:bg-accent-content rounded-lg border shadow-sm">
      {/* Table */}
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead>
            <tr className="border-b bg-gray-50 dark:bg-gray-900/50">
              <th className="px-4 py-3 text-left">
                <Checkbox />
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-muted-foreground">
                Shipped Date
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-muted-foreground">
                Shipment ID
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-muted-foreground">
                Sales Order ID
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-muted-foreground">
                Tracking ID
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-muted-foreground">
                ETD
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-muted-foreground">
                Status
              </th>
              <th className="px-4 py-3 text-left text-sm font-medium text-muted-foreground">
                Actions
              </th>
            </tr>
          </thead>
          <tbody>
            {shipments.map((shipment, index) => (
              <tr
                key={shipment.id}
                className={`border-b hover:bg-gray-50 dark:hover:bg-gray-900/30 transition-colors ${
                  index % 2 === 0 ? "bg-white dark:bg-accent-content" : "bg-gray-50/50 dark:bg-transparent"
                }`}
              >
                <td className="px-4 py-3">
                  <Checkbox />
                </td>
                <td className="px-4 py-3 text-sm text-muted-foreground">
                  {shipment.shippedDate ? formatDateTime(shipment.shippedDate) : "—"}
                </td>
                <td className="px-4 py-3">
                  <div className="relative">
                    <span className="text-sm font-medium text-gray-900 dark:text-gray-100 cursor-help">
                      {shipment.shipmentNumber}
                    </span>
                  </div>
                </td>
                <td className="px-4 py-3">
                  <button
                    onClick={() => navigate(`/sales-order?id=${shipment.salesOrder?.id}`)}
                    className="text-primary-500 hover:underline text-sm"
                  >
                    {shipment.salesOrder?.soNumber || "—"}
                  </button>
                </td>
                <td className="px-4 py-3">
                  {shipment.trackingNumber ? (
                    <a
                      href={`https://track.com/${shipment.trackingNumber}`}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-primary-500 hover:underline text-sm"
                    >
                      {shipment.trackingNumber}
                    </a>
                  ) : (
                    <span className="text-muted-foreground">—</span>
                  )}
                </td>
                <td className="px-4 py-3 text-sm text-muted-foreground">
                  {shipment.estimatedDeliveryDate ? formatDateTime(shipment.estimatedDeliveryDate) : "NA"}
                </td>
                <td className="px-4 py-3">
                  {getStatusBadge(shipment.status)}
                </td>
                <td className="px-4 py-3">
                  {renderActions(shipment)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      <div className="px-4 py-3 border-t flex items-center justify-between">
        <div className="text-sm text-muted-foreground">
          Showing {currentPage * pageSize + 1} to{" "}
          {Math.min((currentPage + 1) * pageSize, totalElements)} of{" "}
          {totalElements} results
        </div>
        {renderPagination()}
      </div>
    </div>
  )
}
