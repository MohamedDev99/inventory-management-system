import { ArrowLeft } from "lucide-react"
import { Filter, Plus } from "lucide-react"
import WarehousesTable from "@/components/warehouses/WarehousesTable"
import AddWarehouseModal from "@/components/warehouses/AddWarehouseModal"
import EditWarehouseModal from "@/components/warehouses/EditWarehouseModal"
import { Button } from "@/components/ui/button"
import { DeleteDialog } from "@/components/ui"
import StatsCard from "@/components/ui/StatsCard"
import TimeFilterBar from "@/components/ui/TimeFilterBar"
import SearchInput from "@/components/ui/SearchInput"
import ExportButton from "@/components/ui/ExportButton"
import { useWarehouses } from "./useWarehouses"

export default function Warehouses() {
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
    selectedWarehouse,
    warehouses,
    totalElements,
    totalPages,
    isLoading,
    deleteWarehouse,
    handleSearch,
    handleAddWarehouse,
    handleEditWarehouse,
    handleDeleteWarehouse,
    openEditModal,
    openDeleteModal,
  } = useWarehouses()

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
        <span>/ Warehouses</span>
      </div>

      {/* Page Title */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-accent-900 dark:text-accent-100">Warehouses</h1>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatsCard
          title="Active Warehouses"
          value={stats.active}
          trend={stats.activeTrend}
          trendLabel="vs last month"
          variant="primary"
        />
        <StatsCard
          title="Inactive Warehouses"
          value={stats.inactive}
          trend={stats.inactiveTrend}
          trendLabel="vs last month"
        />
        <StatsCard
          title="Deleted Warehouses"
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
      <h2 className="text-lg font-semibold text-accent-900 dark:text-accent-100">Active Warehouses</h2>

      {/* Toolbar */}
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <div className="flex items-center gap-3">
          <SearchInput
            value={searchQuery}
            onChange={setSearchQuery}
            onSearch={handleSearch}
            placeholder="Search warehouses..."
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
            className="bg-primary-500 hover:bg-primary-600"
            onClick={() => setIsAddModalOpen(true)}
          >
            <Plus className="w-4 h-4 mr-2" />
            Add Warehouse
          </Button>
        </div>
      </div>

      <WarehousesTable
        warehouses={warehouses}
        loading={isLoading}
        currentPage={currentPage}
        pageSize={pageSize}
        totalElements={totalElements}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        onEdit={openEditModal}
        onDelete={openDeleteModal}
      />

      <AddWarehouseModal 
        open={isAddModalOpen} 
        onOpenChange={setIsAddModalOpen} 
        onSubmit={handleAddWarehouse} 
      />
      
      <EditWarehouseModal 
        open={isEditModalOpen} 
        onOpenChange={setIsEditModalOpen} 
        warehouse={selectedWarehouse} 
        onSubmit={handleEditWarehouse}
        onDelete={openDeleteModal}
      />

      {/* Delete Confirmation Dialog */}
      <DeleteDialog
        open={isDeleteModalOpen}
        onOpenChange={setIsDeleteModalOpen}
        title="Delete Warehouse"
        description="Do you really want to delete the warehouse"
        itemName={selectedWarehouse?.name}
        onConfirm={handleDeleteWarehouse}
        isPending={deleteWarehouse.isPending}
      />
    </div>
  )
}
