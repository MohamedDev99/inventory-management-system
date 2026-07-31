import { useNavigate } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Checkbox } from "@/components/ui/checkbox"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Pagination, PaginationEllipsis, PaginationItem, PaginationLink, PaginationNext, PaginationPrevious } from "@/components/ui/pagination"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { ScheduledReportsDrawer } from "@/components/reports/ScheduledReportsDrawer"
import { useReports } from "./useReports"

export default function Reports() {
  const navigate = useNavigate()
  const {
    activeTab,
    setActiveTab,
    timeFilter,
    setTimeFilter,
    searchQuery,
    setSearchQuery,
    currentPage,
    setCurrentPage,
    pageSize,
    isGenerateModalOpen,
    setIsGenerateModalOpen,
    selectedReportType,
    setSelectedReportType,
    generateForm,
    setGenerateForm,
    isScheduledDrawerOpen,
    setIsScheduledDrawerOpen,
    reports,
    totalElements,
    totalPages,
    isLoading,
    handleSearch,
    handleGenerateReport,
    handleDownload,
    handleDelete,
    reportTypes,
    timeFilters,
  } = useReports()

  const renderPagination = () => {
    const items = []
    const maxVisiblePages = 5
    let startPage = Math.max(0, currentPage - Math.floor(maxVisiblePages / 2))
    const  endPage = Math.min(totalPages - 1, startPage + maxVisiblePages - 1)
    if (endPage - startPage + 1 < maxVisiblePages) startPage = Math.max(0, endPage - maxVisiblePages + 1)
    items.push(<PaginationItem key="prev"><PaginationPrevious onClick={() => setCurrentPage(currentPage - 1)} className={currentPage === 0 ? "pointer-events-none opacity-50" : ""} /></PaginationItem>)
    for (let i = startPage; i <= endPage; i++) items.push(<PaginationItem key={i}><PaginationLink onClick={() => setCurrentPage(i)} className={currentPage === i ? "bg-primary-50" : ""}>{i + 1}</PaginationLink></PaginationItem>)
    if (endPage < totalPages - 1) items.push(<PaginationItem key="ellipsis"><PaginationEllipsis /></PaginationItem>)
    items.push(<PaginationItem key="next"><PaginationNext onClick={() => setCurrentPage(currentPage + 1)} className={currentPage === totalPages - 1 ? "pointer-events-none opacity-50" : ""} /></PaginationItem>)
    return items
  }

  const getStatusBadge = (status: string) => {
    const colors: Record<string, string> = { 
      PENDING: "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-300", 
      PROCESSING: "bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300", 
      COMPLETED: "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-300", 
      FAILED: "bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-300" 
    }
    return <Badge className={colors[status] || "bg-gray-100 dark:bg-gray-800 dark:text-gray-300"}>{status}</Badge>
  }

  return (
    <div className="p-6 space-y-6">
      <div className="flex items-center gap-2 text-sm text-muted-foreground">
        <button onClick={() => navigate(-1)} className="hover:text-primary-500 dark:hover:text-primary-400">← Back</button>
        <span>/ Reports</span>
      </div>
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">Reports</h1>
        <Button variant="outline" size="sm" onClick={() => setIsScheduledDrawerOpen(true)}>Scheduled Reports</Button>
      </div>

      {/* Tabs */}
      <div className="flex gap-2 border-b dark:border-gray-700">
        <button onClick={() => setActiveTab("generate")} className={`px-4 py-2 text-sm font-medium border-b-2 ${activeTab === "generate" ? "border-primary-500 text-primary-600 dark:text-primary-400" : "border-transparent text-muted-foreground hover:text-gray-900 dark:hover:text-gray-100"}`}>Generate Report</button>
        <button onClick={() => setActiveTab("history")} className={`px-4 py-2 text-sm font-medium border-b-2 ${activeTab === "history" ? "border-primary-500 text-primary-600 dark:text-primary-400" : "border-transparent text-muted-foreground hover:text-gray-900 dark:hover:text-gray-100"}`}>Report History</button>
      </div>

      {activeTab === "generate" ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {reportTypes.map((type) => (
            <div key={type.id} className="border dark:border-gray-700 rounded-lg p-4 hover:shadow-md hover:border-primary-300 dark:hover:border-primary-600 transition-all cursor-pointer bg-white dark:bg-accent-content" onClick={() => { setSelectedReportType(type); setIsGenerateModalOpen(true) }}>
              <div className="flex items-start gap-3">
                <div className="bg-primary-100 dark:bg-primary-900/30 p-2 rounded-lg"><svg className="w-5 h-5 text-primary-600 dark:text-primary-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg></div>
                <div className="flex-1">
                  <h3 className="font-medium text-gray-900 dark:text-gray-100">{type.name}</h3>
                  <p className="text-sm text-muted-foreground mt-1">{type.desc}</p>
                  <div className="flex gap-1 mt-2">{type.formats.map((f) => <Badge key={f} variant="outline" className="text-xs dark:border-gray-600 dark:text-gray-300">{f}</Badge>)}</div>
                </div>
              </div>
              <Button size="sm" className="w-full mt-3 bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400">Generate →</Button>
            </div>
          ))}
        </div>
      ) : (
        <>
          <div className="flex items-center gap-1 bg-gray-100 dark:bg-accent-content/20 p-1 rounded-lg w-fit">
            {timeFilters.map((filter) => (<button key={filter} onClick={() => setTimeFilter(filter)} className={`px-3 py-1.5 text-sm rounded-md ${timeFilter === filter ? "bg-white dark:bg-accent-content text-primary shadow-sm font-medium" : "text-muted-foreground hover:text-gray-900 dark:hover:text-gray-100"}`}>{filter}</button>))}
          </div>

          <div className="flex items-center justify-between gap-4 flex-wrap">
            <div className="flex items-center gap-3">
              <div className="relative"><Input type="text" placeholder="Search reports..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} onKeyDown={(e) => e.key === "Enter" && handleSearch()} className="w-64 pl-10 bg-white dark:bg-accent-content border-accent-200 dark:border-accent-700 text-accent-900 dark:text-accent-100" /></div>
              <Button variant="outline" size="sm">Filters</Button>
            </div>
          </div>

          <div className="rounded-md border bg-white dark:bg-accent-content">
            <Table>
              <TableHeader>
                <TableRow className="dark:border-gray-700 dark:bg-gray-900/50">
                  <TableHead className="w-12 dark:text-gray-400"><Checkbox /></TableHead>
                  <TableHead className="dark:text-gray-400">Report Name</TableHead>
                  <TableHead className="dark:text-gray-400">Type</TableHead>
                  <TableHead className="dark:text-gray-400">Format</TableHead>
                  <TableHead className="dark:text-gray-400">Status</TableHead>
                  <TableHead className="dark:text-gray-400">Size</TableHead>
                  <TableHead className="dark:text-gray-400">Generated</TableHead>
                  <TableHead className="dark:text-gray-400">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {isLoading ? [...Array(5)].map((_, i) => (<TableRow key={i} className="dark:border-gray-700"><TableCell colSpan={8}><div className="h-4 bg-gray-200 dark:bg-gray-700 animate-pulse rounded" /></TableCell></TableRow>)) :
                  reports.length === 0 ? (<TableRow className="dark:border-gray-700"><TableCell colSpan={8} className="h-32 text-center text-muted-foreground">No reports generated yet.</TableCell></TableRow>) :
                    reports.map((report) => (
                      <TableRow key={report.id} className="dark:border-gray-700 dark:hover:bg-gray-900/30">
                        <TableCell><Checkbox /></TableCell>
                        <TableCell className="font-medium dark:text-gray-100">{report.name}</TableCell>
                        <TableCell className="dark:text-gray-300"><Badge variant="outline" className="dark:border-gray-600 dark:text-gray-300">{report.type}</Badge></TableCell>
                        <TableCell className="dark:text-gray-300"><Badge variant="outline" className="dark:border-gray-600 dark:text-gray-300">{report.format}</Badge></TableCell>
                        <TableCell>{getStatusBadge(report.status)}</TableCell>
                        <TableCell className="dark:text-gray-300">{report.fileSize || "-"}</TableCell>
                        <TableCell className="dark:text-gray-300">{report.generatedAt ? new Date(report.generatedAt).toLocaleDateString() : "-"}</TableCell>
                        <TableCell>
                          <div className="flex items-center gap-1">
                            <Button variant="ghost" size="icon" className="h-8 w-8 dark:hover:bg-gray-800" disabled={report.status !== "COMPLETED"} onClick={() => handleDownload(report)}>
                              <svg className="w-4 h-4 dark:text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 10v6m0 0l-3-3m3 3l3-3m2 8H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" /></svg>
                            </Button>
                            <Button variant="ghost" size="icon" className="h-8 w-8 text-red-500 hover:text-red-600 dark:text-red-400 dark:hover:text-red-300" onClick={() => handleDelete(report.id)}>
                              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
              </TableBody>
            </Table>
          </div>

          <div className="flex items-center justify-between">
            <div className="text-sm text-muted-foreground">Showing {currentPage * pageSize + 1} to {Math.min((currentPage + 1) * pageSize, totalElements)} of {totalElements}</div>
            <Pagination>{renderPagination()}</Pagination>
          </div>
        </>
      )}

      <Dialog open={isGenerateModalOpen} onOpenChange={setIsGenerateModalOpen}>
        <DialogContent className="sm:max-w-[500px] dark:bg-accent-content">
          <DialogHeader>
            <DialogTitle className="dark:text-gray-100">Generate {selectedReportType?.name}</DialogTitle>
            <DialogDescription className="dark:text-gray-400">{selectedReportType?.desc}</DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label className="dark:text-gray-100">Report Name</Label>
              <Input value={generateForm.name} onChange={(e) => setGenerateForm({ ...generateForm, name: e.target.value })} placeholder={`${selectedReportType?.name} - ${new Date().toLocaleDateString()}`} className="dark:bg-accent-800 dark:border-gray-700 dark:text-gray-100" />
            </div>
            <div className="grid gap-2">
              <Label className="dark:text-gray-100">Output Format</Label>
              <Select value={generateForm.format} onValueChange={(value: "PDF" | "EXCEL" | "CSV") => setGenerateForm({ ...generateForm, format: value })}>
                <SelectTrigger className="dark:bg-accent-800 dark:border-gray-700 dark:text-gray-100"><SelectValue /></SelectTrigger>
                <SelectContent className="dark:bg-accent-800 dark:border-gray-700">
                  {selectedReportType?.formats.map((f) => <SelectItem key={f} value={f} className="dark:text-gray-100">{f}</SelectItem>)}
                </SelectContent>
              </Select>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div className="grid gap-2">
                <Label className="dark:text-gray-100">Start Date</Label>
                <Input type="date" value={generateForm.startDate} onChange={(e) => setGenerateForm({ ...generateForm, startDate: e.target.value })} className="dark:bg-accent-800 dark:border-gray-700 dark:text-gray-100" />
              </div>
              <div className="grid gap-2">
                <Label className="dark:text-gray-100">End Date</Label>
                <Input type="date" value={generateForm.endDate} onChange={(e) => setGenerateForm({ ...generateForm, endDate: e.target.value })} className="dark:bg-accent-800 dark:border-gray-700 dark:text-gray-100" />
              </div>
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsGenerateModalOpen(false)} className="dark:border-gray-700 dark:text-gray-100">Cancel</Button>
            <Button className="bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400" onClick={handleGenerateReport}>Generate Report</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      <ScheduledReportsDrawer
        open={isScheduledDrawerOpen}
        onOpenChange={setIsScheduledDrawerOpen}
      />
    </div>
  )
}
