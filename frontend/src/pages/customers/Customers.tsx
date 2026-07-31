import { ArrowLeft } from "lucide-react"
import { Filter, Plus } from "lucide-react"
import CustomersTable from "@/components/customers/CustomersTable"
import AddCustomerModal from "@/components/customers/AddCustomerModal"
import EditCustomerModal from "@/components/customers/EditCustomerModal"
import { Button } from "@/components/ui/button"
import { DeleteDialog } from "@/components/ui"
import StatsCard from "@/components/ui/StatsCard"
import TimeFilterBar from "@/components/ui/TimeFilterBar"
import SearchInput from "@/components/ui/SearchInput"
import ExportButton from "@/components/ui/ExportButton"
import { useCustomers } from "./useCustomers"

export default function Customers() {
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
    selectedCustomer,
    customers,
    totalElements,
    totalPages,
    isLoading,
    deleteCustomer,
    handleSearch,
    handleAddCustomer,
    handleEditCustomer,
    handleDeleteCustomer,
    openEditModal,
    openDeleteModal,
  } = useCustomers()

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
        <span>/ Customers</span>
      </div>

      {/* Page Title */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-accent-900 dark:text-accent-100">Customers</h1>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatsCard
          title="Active Customers"
          value={stats.active}
          trend={stats.activeTrend}
          trendLabel="vs last month"
          variant="primary"
        />
        <StatsCard
          title="Inactive Customers"
          value={stats.inactive}
          trend={stats.inactiveTrend}
          trendLabel="vs last month"
        />
        <StatsCard
          title="Deleted Customers"
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
      <h2 className="text-lg font-semibold text-accent-900 dark:text-accent-100">Active Customers</h2>

      {/* Toolbar */}
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <div className="flex items-center gap-3">
          <SearchInput
            value={searchQuery}
            onChange={setSearchQuery}
            onSearch={handleSearch}
            placeholder="Search customers..."
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
            Add Customer
          </Button>
        </div>
      </div>

      {/* Customers Table */}
      <CustomersTable
        customers={customers}
        loading={isLoading}
        currentPage={currentPage}
        pageSize={pageSize}
        totalElements={totalElements}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        onEdit={openEditModal}
        onDelete={openDeleteModal}
      />

      {/* Add Customer Modal */}
      <AddCustomerModal
        open={isAddModalOpen}
        onOpenChange={setIsAddModalOpen}
        onSubmit={handleAddCustomer}
      />

      {/* Edit Customer Modal */}
      <EditCustomerModal
        open={isEditModalOpen}
        onOpenChange={setIsEditModalOpen}
        customer={selectedCustomer}
        onSubmit={handleEditCustomer}
      />

      {/* Delete Confirmation Dialog */}
      <DeleteDialog
        open={isDeleteModalOpen}
        onOpenChange={setIsDeleteModalOpen}
        title="Delete Customer"
        description="Do you really want to delete the customer"
        itemName={selectedCustomer?.companyName}
        onConfirm={handleDeleteCustomer}
        isPending={deleteCustomer.isPending}
      />
    </div>
  )
}
