import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Checkbox } from "@/components/ui/checkbox"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import {
  Pagination,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from "@/components/ui/tooltip"
import type { Department } from "@/types"

interface DepartmentsTableProps {
  departments: Department[]
  loading: boolean
  currentPage: number
  pageSize: number
  totalElements: number
  totalPages: number
  onPageChange: (page: number) => void
  onEdit: (department: Department) => void
  onDelete: (department: Department) => void
}

export default function DepartmentsTable({
  departments,
  loading,
  currentPage,
  pageSize,
  totalElements,
  totalPages,
  onPageChange,
  onEdit,
  onDelete,
}: DepartmentsTableProps) {
  const renderPagination = () => {
    const items = []
    const maxVisiblePages = 5
    let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2))
    let endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1)

    if (endPage - startPage + 1 < maxVisiblePages) {
      startPage = Math.max(0, endPage - maxVisiblePages + 1)
    }

    items.push(
      <PaginationItem key="prev">
        <PaginationPrevious
          onClick={() => onPageChange(currentPage - 1)}
          className={currentPage === 0 ? "pointer-events-none opacity-50" : "cursor-pointer"}
        />
      </PaginationItem>
    )

    for (let i = startPage; i <= endPage; i++) {
      items.push(
        <PaginationItem key={i}>
          <PaginationLink
            onClick={() => onPageChange(i)}
            className={currentPage === i ? "bg-primary-50 text-primary-600" : "cursor-pointer"}
          >
            {i + 1}
          </PaginationLink>
        </PaginationItem>
      )
    }

    if (endPage < totalPages - 1) {
      items.push(
        <PaginationItem key="ellipsis">
          <PaginationEllipsis />
        </PaginationItem>
      )
    }

    items.push(
      <PaginationItem key="next">
        <PaginationNext
          onClick={() => onPageChange(currentPage + 1)}
          className={currentPage === totalPages - 1 ? "pointer-events-none opacity-50" : "cursor-pointer"}
        />
      </PaginationItem>
    )

    return items
  }

  if (loading) {
    return (
      <div className="rounded-md border bg-white dark:bg-accent-content">
        <Table>
          <TableHeader>
            <TableRow className="dark:border-accent-700">
              <TableHead className="w-12"><Checkbox /></TableHead>
              <TableHead className="dark:text-accent-400">Department Name</TableHead>
              <TableHead className="dark:text-accent-400">Department ID</TableHead>
              <TableHead className="dark:text-accent-400">Warehouse</TableHead>
              <TableHead className="dark:text-accent-400">Manager</TableHead>
              <TableHead className="dark:text-accent-400">Email</TableHead>
              <TableHead className="dark:text-accent-400">Phone</TableHead>
              <TableHead className="dark:text-accent-400">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {[...Array(5)].map((_, i) => (
              <TableRow key={i} className="dark:border-accent-700">
                <TableCell><div className="h-4 w-4 bg-gray-200 dark:bg-gray-700 animate-pulse rounded" /></TableCell>
                <TableCell><div className="h-4 w-32 bg-gray-200 dark:bg-gray-700 animate-pulse rounded" /></TableCell>
                <TableCell><div className="h-4 w-24 bg-gray-200 dark:bg-gray-700 animate-pulse rounded" /></TableCell>
                <TableCell><div className="h-4 w-24 bg-gray-200 dark:bg-gray-700 animate-pulse rounded" /></TableCell>
                <TableCell><div className="h-4 w-24 bg-gray-200 dark:bg-gray-700 animate-pulse rounded" /></TableCell>
                <TableCell><div className="h-4 w-32 bg-gray-200 dark:bg-gray-700 animate-pulse rounded" /></TableCell>
                <TableCell><div className="h-4 w-24 bg-gray-200 dark:bg-gray-700 animate-pulse rounded" /></TableCell>
                <TableCell><div className="h-8 w-20 bg-gray-200 dark:bg-gray-700 animate-pulse rounded" /></TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    )
  }

  if (departments.length === 0) {
    return (
      <div className="rounded-md border bg-white dark:bg-accent-content">
        <Table>
          <TableHeader>
            <TableRow className="dark:border-accent-700">
              <TableHead className="w-12"><Checkbox /></TableHead>
              <TableHead className="dark:text-accent-400">Department Name</TableHead>
              <TableHead className="dark:text-accent-400">Department ID</TableHead>
              <TableHead className="dark:text-accent-400">Warehouse</TableHead>
              <TableHead className="dark:text-accent-400">Manager</TableHead>
              <TableHead className="dark:text-accent-400">Email</TableHead>
              <TableHead className="dark:text-accent-400">Phone</TableHead>
              <TableHead className="dark:text-accent-400">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow className="dark:border-accent-700">
              <TableCell colSpan={8} className="h-32 text-center text-muted-foreground">
                No departments found. Add a new department to get started.
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>
    )
  }

  return (
    <div className="space-y-4">
      <div className="rounded-md border bg-white dark:bg-accent-content">
        <Table>
          <TableHeader>
            <TableRow className="dark:border-accent-700 dark:bg-accent-900/50">
              <TableHead className="w-12 dark:text-accent-400"><Checkbox /></TableHead>
              <TableHead className="dark:text-accent-400">Department Name</TableHead>
              <TableHead className="dark:text-accent-400">Department ID</TableHead>
              <TableHead className="dark:text-accent-400">Warehouse</TableHead>
              <TableHead className="dark:text-accent-400">Manager</TableHead>
              <TableHead className="dark:text-accent-400">Email</TableHead>
              <TableHead className="dark:text-accent-400">Phone</TableHead>
              <TableHead className="dark:text-accent-400">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {departments.map((department) => (
              <TableRow key={department.id} className="dark:border-accent-700 dark:hover:bg-accent-900/30">
                <TableCell><Checkbox /></TableCell>
                <TableCell className="font-medium dark:text-gray-100">{department.name}</TableCell>
                <TableCell>
                  <Badge variant="outline" className="font-mono dark:border-accent-600 dark:text-accent-300">{department.code}</Badge>
                </TableCell>
                <TableCell className="dark:text-gray-300">{department.warehouse?.name || "-"}</TableCell>
                <TableCell className="dark:text-gray-300">
                  {department.manager ? (
                    <TooltipProvider>
                      <Tooltip>
                        <TooltipTrigger asChild>
                          <span className="cursor-help">{department.manager.username}</span>
                        </TooltipTrigger>
                        <TooltipContent className="dark:bg-accent-800 dark:text-accent-100">
                          <p>{department.manager.email}</p>
                        </TooltipContent>
                      </Tooltip>
                    </TooltipProvider>
                  ) : "-"}
                </TableCell>
                <TableCell className="dark:text-gray-300">
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger asChild>
                        <span className="cursor-help truncate block max-w-[150px]">
                          {department.email || "-"}
                        </span>
                      </TooltipTrigger>
                      <TooltipContent className="dark:bg-accent-800 dark:text-accent-100">
                        <p>{department.email}</p>
                      </TooltipContent>
                    </Tooltip>
                  </TooltipProvider>
                </TableCell>
                <TableCell className="dark:text-gray-300">{department.phone || "-"}</TableCell>
                <TableCell>
                  <div className="flex items-center gap-1">
                    <Button variant="ghost" size="icon" className="h-8 w-8 dark:hover:bg-accent-800" onClick={() => onEdit(department)}>
                      <svg className="w-4 h-4 dark:text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                      </svg>
                    </Button>
                    <Button variant="ghost" size="icon" className="h-8 w-8 text-red-500 hover:text-red-600 dark:text-red-400 dark:hover:text-red-300" onClick={() => onDelete(department)}>
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      <div className="flex items-center justify-between">
        <div className="text-sm text-muted-foreground">
          Showing {currentPage * pageSize + 1} to {Math.min((currentPage + 1) * pageSize, totalElements)} of {totalElements} entries
        </div>
        <Pagination>{renderPagination()}</Pagination>
      </div>
    </div>
  )
}
