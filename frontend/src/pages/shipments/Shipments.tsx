import { useNavigate } from "react-router-dom"
import ShipmentsTable from "@/components/shipments/ShipmentsTable"
import CreateShipmentModal from "@/components/shipments/CreateShipmentModal"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { useShipments } from "./useShipments"

type TimeFilter = "1d" | "7d" | "1m" | "3m" | "6m" | "1y" | "3y" | "5y"

const timeFilters: TimeFilter[] = ["1d", "7d", "1m", "3m", "6m", "1y", "3y", "5y"]

export default function Shipments() {
  const navigate = useNavigate()
  const {
    timeFilter,
    setTimeFilter,
    searchQuery,
    setSearchQuery,
    currentPage,
    setCurrentPage,
    pageSize,
    shipments,
    totalElements,
    totalPages,
    isLoading,
    isCreateModalOpen,
    setIsCreateModalOpen,
    handleSearch,
    handleCreate,
    handleUpdateStatus,
    handleDeliver,
  } = useShipments()

  const handlePageChange = (page: number) => setCurrentPage(page)

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center gap-2 text-sm text-accent-500"><button onClick={() => navigate(-1)} className="hover:text-primary-500">← Back</button><span>/ Shipments</span></div>
      <h1 className="text-2xl font-bold text-accent-900">Shipments</h1>

      <div className="flex items-center gap-1 bg-gray-100 p-1 rounded-lg w-fit">
        {timeFilters.map((f) => <button key={f} onClick={() => setTimeFilter(f)} className={`px-3 py-1.5 text-sm rounded-md ${timeFilter === f ? "bg-white text-primary-500 shadow-sm" : "text-accent-500"}`}>{f}</button>)}
      </div>

      <div className="flex items-center justify-between">
        <div className="flex gap-3"><Input placeholder="Search shipments..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} onKeyDown={(e) => e.key === "Enter" && handleSearch()} className="w-64" /><Button variant="outline">Filters</Button></div>
        <div className="flex gap-3"><Button variant="outline">Export</Button><Button className="bg-primary-500" onClick={() => setIsCreateModalOpen(true)}>+ Create Shipment</Button></div>
      </div>

      <ShipmentsTable 
        shipments={shipments} 
        loading={isLoading} 
        currentPage={currentPage} 
        pageSize={pageSize} 
        totalElements={totalElements} 
        totalPages={totalPages} 
        onPageChange={handlePageChange} 
        onUpdateStatus={handleUpdateStatus} 
        onDeliver={handleDeliver} 
      />

      <CreateShipmentModal
        open={isCreateModalOpen}
        onOpenChange={setIsCreateModalOpen}
        onSubmit={handleCreate}
      />
    </div>
  )
}
