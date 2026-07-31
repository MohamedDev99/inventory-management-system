import { ArrowLeft } from "lucide-react"
import { Filter, Plus } from "lucide-react"
import SuppliersTable from "@/components/suppliers/SuppliersTable"
import AddSupplierModal from "@/components/suppliers/AddSupplierModal"
import EditSupplierModal from "@/components/suppliers/EditSupplierModal"
import { Button } from "@/components/ui/button"
import { DeleteDialog } from "@/components/ui"
import StatsCard from "@/components/ui/StatsCard"
import TimeFilterBar from "@/components/ui/TimeFilterBar"
import SearchInput from "@/components/ui/SearchInput"
import ExportButton from "@/components/ui/ExportButton"
import { useSuppliers } from "./useSuppliers"

export default function Suppliers() {
  const {
    timeFilter,
    setTimeFilter,
    searchQuery,
    setSearchQuery,
    currentPage,
    setCurrentPage,
    pageSize,
    stats,
    isAddModalOpen,
    setIsAddModalOpen,
    isEditModalOpen,
    setIsEditModalOpen,
    isDeleteModalOpen,
    setIsDeleteModalOpen,
    selectedSupplier,
    suppliers,
    totalElements,
    totalPages,
    isLoading,
    deleteSupplier,
    handleSearch,
    handleAddSupplier,
    handleEditSupplier,
    handleDeleteSupplier,
    openEditModal,
    openDeleteModal,
  } = useSuppliers()

  return (
    <div className="p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center gap-2 text-sm text-accent-500 dark:text-accent-400">
        <button 
          onClick={() => window.history.back()} 
          className="hover:text-primary-500 dark:hover:text-primary-400 flex items-center gap-1"
        >
          <ArrowLeft className="w-4 h-4" />
          Back
        </button>
        <span>/ Suppliers</span>
      </div>

      {/* Page Title */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-accent-900 dark:text-accent-100">Suppliers</h1>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatsCard
          title="Active Suppliers"
          value={stats.active}
          trend={stats.activeTrend}
          trendLabel="vs last month"
          variant="primary"
          className="bg-primary-500"
        />
        <StatsCard
          title="Inactive Suppliers"
          value={stats.inactive}
          trend={stats.inactiveTrend}
          trendLabel="vs last month"
        />
        <StatsCard
          title="Deleted Suppliers"
          value={stats.deleted}
          trend={stats.deletedTrend}
          trendLabel="vs last month"
        />
      </div>

      {/* Time Filter Bar */}
      <TimeFilterBar
        value={timeFilter}
        onChange={setTimeFilter}
        showSelectDates={false}
      />

      {/* Section Title */}
      <h2 className="text-lg font-semibold text-accent-900 dark:text-accent-100">Active Suppliers</h2>

      {/* Toolbar */}
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <div className="flex items-center gap-3">
          <SearchInput
            value={searchQuery}
            onChange={setSearchQuery}
            onSearch={handleSearch}
            placeholder="Search suppliers..."
          />
          <Button variant="outline" size="sm">
            <Filter className="w-4 h-4 mr-2" />
            Filters
          </Button>
        </div>

        <div className="flex items-center gap-3">
          <ExportButton />
          <Button
            size="sm"
            className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400"
            onClick={() => setIsAddModalOpen(true)}
          >
            <Plus className="w-4 h-4 mr-2" />
            Add New Supplier
          </Button>
        </div>
      </div>

      {/* Suppliers Table */}
      <SuppliersTable
        suppliers={suppliers}
        loading={isLoading}
        currentPage={currentPage}
        pageSize={pageSize}
        totalElements={totalElements}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        onEdit={openEditModal}
        onDelete={openDeleteModal}
      />

      {/* Add Supplier Modal */}
      <AddSupplierModal
        open={isAddModalOpen}
        onOpenChange={setIsAddModalOpen}
        onSubmit={handleAddSupplier}
      />

      {/* Edit Supplier Modal */}
      <EditSupplierModal
        open={isEditModalOpen}
        onOpenChange={setIsEditModalOpen}
        supplier={selectedSupplier}
        onSubmit={handleEditSupplier}
      />

      {/* Delete Confirmation Dialog */}
      <DeleteDialog
        open={isDeleteModalOpen}
        onOpenChange={setIsDeleteModalOpen}
        title="Delete Supplier"
        description="Do you really want to delete the supplier"
        itemName={selectedSupplier?.name}
        onConfirm={handleDeleteSupplier}
        isPending={deleteSupplier.isPending}
      />
    </div>
  )
}
