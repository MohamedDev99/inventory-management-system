import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import InvoicesTable from "@/components/invoices/InvoicesTable"
import CreateInvoiceModal from "@/components/invoices/CreateInvoiceModal"
import { useInvoices } from "./useInvoices"

type TimeFilter = "1d" | "7d" | "1m" | "3m" | "6m" | "1y" | "3y" | "5y"

const timeFilters: TimeFilter[] = ["1d", "7d", "1m", "3m", "6m", "1y", "3y", "5y"]

export default function Invoices() {
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
    invoices,
    totalElements,
    totalPages,
    isLoading,
    handleSearch,
    handleCreate,
  } = useInvoices()

  const handlePageChange = (page: number) => setCurrentPage(page)

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center gap-2 text-sm text-accent-500"><button onClick={() => navigate(-1)} className="hover:text-primary-500">← Back</button><span>/ Invoices</span></div>
      <h1 className="text-2xl font-bold text-accent-900">Invoices</h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-primary-500 text-white rounded-lg p-4"><p className="text-sm">Total Invoices</p><p className="text-2xl font-bold">{totalElements}</p></div>
        <div className="bg-white rounded-lg p-4 border"><p className="text-sm text-gray-500">Overdue</p><p className="text-2xl font-bold">0</p></div>
        <div className="bg-white rounded-lg p-4 border"><p className="text-sm text-gray-500">Paid</p><p className="text-2xl font-bold">0</p></div>
      </div>

      <div className="flex items-center gap-1 bg-gray-100 p-1 rounded-lg w-fit">
        {timeFilters.map((f) => <button key={f} onClick={() => setTimeFilter(f)} className={`px-3 py-1.5 text-sm rounded-md ${timeFilter === f ? "bg-white text-primary-500 shadow-sm" : "text-accent-500"}`}>{f}</button>)}
      </div>

      {/* Section Title */}
      <h2 className="text-lg font-semibold text-accent-900 dark:text-accent-100">All Invoices</h2>

      <div className="flex items-center justify-between">
        <div className="flex gap-3"><Input placeholder="Search invoices..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} onKeyDown={(e) => e.key === "Enter" && handleSearch()} className="w-64" /><Button variant="outline">Filters</Button></div>
        <Button className="bg-primary-500" onClick={() => setIsCreateModalOpen(true)}>+ Create Invoice</Button>
      </div>

      {/* Invoices Table */}
      <InvoicesTable
        invoices={invoices}
        loading={isLoading}
        currentPage={currentPage}
        pageSize={pageSize}
        totalElements={totalElements}
        totalPages={totalPages}
        onPageChange={handlePageChange}
      />

      {/* Create Invoice Modal */}
      <CreateInvoiceModal
        open={isCreateModalOpen}
        onOpenChange={setIsCreateModalOpen}
        onSubmit={handleCreate}
      />
    </div>
  )
}
