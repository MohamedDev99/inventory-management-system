import { ArrowLeft } from "lucide-react"
import { Filter, Plus } from "lucide-react"
import DepartmentsTable from "@/components/departments/DepartmentsTable"
import AddDepartmentModal from "@/components/departments/AddDepartmentModal"
import EditDepartmentModal from "@/components/departments/EditDepartmentModal"
import { Button } from "@/components/ui/button"
import { DeleteDialog } from "@/components/ui"
import StatsCard from "@/components/ui/StatsCard"
import TimeFilterBar from "@/components/ui/TimeFilterBar"
import SearchInput from "@/components/ui/SearchInput"
import ExportButton from "@/components/ui/ExportButton"
import { useDepartments } from "./useDepartments"

export default function Departments() {
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
    selectedDepartment,
    departments,
    totalElements,
    totalPages,
    isLoading,
    deleteDepartment,
    handleSearch,
    handleAddDepartment,
    handleEditDepartment,
    handleDeleteDepartment,
    openEditModal,
    openDeleteModal,
  } = useDepartments()

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
        <span>/ Departments</span>
      </div>

      {/* Page Title */}
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-accent-900 dark:text-accent-100">Departments</h1>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatsCard
          title="Active Departments"
          value={stats.active}
          trend={stats.activeTrend}
          trendLabel="vs last month"
          variant="primary"
        />
        <StatsCard
          title="Inactive Departments"
          value={stats.inactive}
          trend={stats.inactiveTrend}
          trendLabel="vs last month"
        />
        <StatsCard
          title="Deleted Departments"
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
      <h2 className="text-lg font-semibold text-accent-900 dark:text-accent-100">Active Departments</h2>

      {/* Toolbar */}
      <div className="flex items-center justify-between gap-4 flex-wrap">
        <div className="flex items-center gap-3">
          <SearchInput
            value={searchQuery}
            onChange={setSearchQuery}
            onSearch={handleSearch}
            placeholder="Search departments..."
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
            Add Department
          </Button>
        </div>
      </div>

      <DepartmentsTable 
        departments={departments} 
        loading={isLoading} 
        currentPage={currentPage} 
        pageSize={pageSize} 
        totalElements={totalElements} 
        totalPages={totalPages} 
        onPageChange={setCurrentPage} 
        onEdit={openEditModal} 
        onDelete={openDeleteModal} 
      />

      <AddDepartmentModal open={isAddModalOpen} onOpenChange={setIsAddModalOpen} onSubmit={handleAddDepartment} />
      <EditDepartmentModal open={isEditModalOpen} onOpenChange={setIsEditModalOpen} department={selectedDepartment} onSubmit={handleEditDepartment} />

      {/* Delete Confirmation Dialog */}
      <DeleteDialog
        open={isDeleteModalOpen}
        onOpenChange={setIsDeleteModalOpen}
        title="Delete Department"
        description="Do you really want to delete the department"
        itemName={selectedDepartment?.name}
        onConfirm={handleDeleteDepartment}
        isPending={deleteDepartment.isPending}
      />
    </div>
  )
}
