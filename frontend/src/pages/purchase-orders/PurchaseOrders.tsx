import { useNavigate } from "react-router-dom"
import PurchaseOrdersTable from "@/components/purchase-orders/PurchaseOrdersTable"
import CreatePOModal from "@/components/purchase-orders/CreatePOModal"
import POConfirmationAlert from "@/components/purchase-orders/POConfirmationAlert"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { usePurchaseOrders } from "./usePurchaseOrders"

type TimeFilter = "1d" | "7d" | "1m" | "3m" | "6m" | "1y" | "3y" | "5y"

const timeFilters: TimeFilter[] = ["1d", "7d", "1m", "3m", "6m", "1y", "3y", "5y"]

export default function PurchaseOrders() {
  const navigate = useNavigate()
  const {
    timeFilter,
    setTimeFilter,
    searchQuery,
    setSearchQuery,
    currentPage,
    setCurrentPage,
    pageSize,
    isCreateModalOpen,
    setIsCreateModalOpen,
    isConfirmAlertOpen,
    setIsConfirmAlertOpen,
    pendingOrderData,
    orders,
    totalElements,
    totalPages,
    isLoading,
    handleSearch,
    handleCreateOrder,
    handleSubmit,
    handleApprove,
    handleReject,
    handleReceive,
    handleConfirmReceive,
    deletePO,
  } = usePurchaseOrders()

  const handlePageChange = (page: number) => setCurrentPage(page)

  const handleDelete = async (id: number) => {
    await deletePO.mutateAsync(id)
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center gap-2 text-sm text-accent-500"><button onClick={() => navigate(-1)} className="hover:text-primary-500">← Back</button><span>/ Purchase Orders</span></div>
      <h1 className="text-2xl font-bold text-accent-900">Purchase Orders</h1>

      <div className="flex items-center gap-1 bg-gray-100 p-1 rounded-lg w-fit">
        {timeFilters.map((f) => <button key={f} onClick={() => setTimeFilter(f)} className={`px-3 py-1.5 text-sm rounded-md ${timeFilter === f ? "bg-white text-primary-500 shadow-sm" : "text-accent-500"}`}>{f}</button>)}
      </div>

      <div className="flex items-center justify-between">
        <div className="flex gap-3"><Input placeholder="Search orders..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} onKeyDown={(e) => e.key === "Enter" && handleSearch()} className="w-64" /><Button variant="outline">Filters</Button></div>
        <div className="flex gap-3"><Button variant="outline">Export</Button><Button className="bg-primary-500" onClick={() => setIsCreateModalOpen(true)}>+ Create PO</Button></div>
      </div>

      <PurchaseOrdersTable 
        orders={orders} 
        loading={isLoading} 
        currentPage={currentPage} 
        pageSize={pageSize} 
        totalElements={totalElements} 
        totalPages={totalPages} 
        onPageChange={handlePageChange}
        onSubmit={handleSubmit}
        onApprove={handleApprove}
        onReject={handleReject}
        onReceive={handleReceive}
        onDelete={handleDelete}
      />

      <CreatePOModal open={isCreateModalOpen} onOpenChange={setIsCreateModalOpen} onSubmit={handleCreateOrder} />
      <POConfirmationAlert open={isConfirmAlertOpen} onOpenChange={setIsConfirmAlertOpen} orderData={pendingOrderData} onConfirm={handleConfirmReceive} />
    </div>
  )
}
