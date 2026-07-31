import { useNavigate } from "react-router-dom"
import SalesOrdersTable from "@/components/sales-orders/SalesOrdersTable"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useSalesOrders } from "./useSalesOrders"

type TimeFilter = "1d" | "7d" | "1m" | "3m" | "6m" | "1y" | "3y" | "5y"

const timeFilters: TimeFilter[] = ["1d", "7d", "1m", "3m", "6m", "1y", "3y", "5y"]

export default function SalesOrders() {
  const navigate = useNavigate()
  const {
    timeFilter,
    setTimeFilter,
    searchQuery,
    setSearchQuery,
    currentPage,
    setCurrentPage,
    pageSize,
    orders,
    totalElements,
    totalPages,
    isLoading,
    handleSearch,
    handleConfirm,
    handleFulfill,
    handleShip,
    handleDeliver,
    handleCancel,
    handleDelete,
  } = useSalesOrders()

  const handlePageChange = (page: number) => setCurrentPage(page)

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center gap-2 text-sm text-accent-500"><button onClick={() => navigate(-1)} className="hover:text-primary-500">← Back</button><span>/ Sales Orders</span></div>
      <h1 className="text-2xl font-bold text-accent-900">Sales Orders</h1>

      <div className="flex items-center gap-1 bg-gray-100 p-1 rounded-lg w-fit">
        {timeFilters.map((f) => <button key={f} onClick={() => setTimeFilter(f)} className={`px-3 py-1.5 text-sm rounded-md ${timeFilter === f ? "bg-white text-primary-500 shadow-sm" : "text-accent-500"}`}>{f}</button>)}
      </div>

      <div className="flex items-center justify-between">
        <div className="flex gap-3"><Input placeholder="Search orders..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} onKeyDown={(e) => e.key === "Enter" && handleSearch()} className="w-64" /><Button variant="outline">Filters</Button></div>
        <div className="flex gap-3"><Button variant="outline">Export</Button><Button className="bg-primary-500">+ Create SO</Button></div>
      </div>

      <SalesOrdersTable orders={orders} loading={isLoading} currentPage={currentPage} pageSize={pageSize} totalElements={totalElements} totalPages={totalPages} onPageChange={handlePageChange} onConfirm={handleConfirm} onFulfill={handleFulfill} onShip={handleShip} onDeliver={handleDeliver} onCancel={handleCancel} onDelete={handleDelete} />
    </div>
  )
}
