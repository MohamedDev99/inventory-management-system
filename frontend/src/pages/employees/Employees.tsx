import { ArrowLeft } from "lucide-react"
import { Filter, Plus } from "lucide-react"
import EmployeesTable from "@/components/employees/EmployeesTable"
import AddEmployeeModal from "@/components/employees/AddEmployeeModal"
import EditEmployeeModal from "@/components/employees/EditEmployeeModal"
import { Button } from "@/components/ui/button"
import { DeleteDialog } from "@/components/ui"
import StatsCard from "@/components/ui/StatsCard"
import TimeFilterBar from "@/components/ui/TimeFilterBar"
import SearchInput from "@/components/ui/SearchInput"
import ExportButton from "@/components/ui/ExportButton"
import { useEmployees } from "./useEmployees"

export default function Employees() {
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
    selectedEmployee,
    employees,
    totalElements,
    totalPages,
    isLoading,
    deleteEmployee,
    handleSearch,
    handleAddEmployee,
    handleEditEmployee,
    handleDeleteEmployee,
    openEditModal,
    openDeleteModal,
  } = useEmployees()

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
        <span>/ Employees</span>
      </div>

      {/* Page Title */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-accent-900 dark:text-accent-100">Employees</h1>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatsCard
          title="Active Employees"
          value={stats.active}
          trend={stats.activeTrend}
          trendLabel="vs last month"
          variant="primary"
        />
        <StatsCard
          title="Inactive Employees"
          value={stats.inactive}
          trend={stats.inactiveTrend}
          trendLabel="vs last month"
        />
        <StatsCard
          title="Deleted Employees"
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
      <h2 className="text-lg font-semibold text-accent-900 dark:text-accent-100">Active Employees</h2>

      {/* Toolbar */}
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <div className="flex items-center gap-3">
          <SearchInput
            value={searchQuery}
            onChange={setSearchQuery}
            onSearch={handleSearch}
            placeholder="Search employees..."
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
            Add Employee
          </Button>
        </div>
      </div>

      <EmployeesTable 
        employees={employees} 
        loading={isLoading} 
        currentPage={currentPage} 
        pageSize={pageSize} 
        totalElements={totalElements} 
        totalPages={totalPages} 
        onPageChange={setCurrentPage} 
        onEdit={openEditModal} 
        onDelete={openDeleteModal} 
      />

      <AddEmployeeModal open={isAddModalOpen} onOpenChange={setIsAddModalOpen} onSubmit={handleAddEmployee} />
      <EditEmployeeModal open={isEditModalOpen} onOpenChange={setIsEditModalOpen} employee={selectedEmployee} onSubmit={handleEditEmployee} />

      {/* Delete Confirmation Dialog */}
      <DeleteDialog
        open={isDeleteModalOpen}
        onOpenChange={setIsDeleteModalOpen}
        title="Delete Employee"
        description="Do you really want to delete the employee"
        itemName={selectedEmployee?.firstName}
        onConfirm={handleDeleteEmployee}
        isPending={deleteEmployee.isPending}
      />
    </div>
  )
}
