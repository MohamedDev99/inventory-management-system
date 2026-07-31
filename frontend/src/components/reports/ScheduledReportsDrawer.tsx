import { useState, useEffect } from "react"
import { Badge } from "@/components/ui/badge"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import api from "@/api/axios"
import type { Report } from "@/types"

interface ScheduledReportsDrawerProps {
  open: boolean
  onOpenChange: (open: boolean) => void
}

export function ScheduledReportsDrawer({ open, onOpenChange }: ScheduledReportsDrawerProps) {
  const [scheduledReports, setScheduledReports] = useState<Report[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (open) {
      fetchScheduledReports()
    }
  }, [open])

  const fetchScheduledReports = async () => {
    setLoading(true)
    try {
      const response = await api.get("/reports/scheduled")
      setScheduledReports(response.data.data || [])
    } catch (error) {
      console.error("Failed to fetch scheduled reports:", error)
    } finally {
      setLoading(false)
    }
  }

  const getStatusBadge = (status: string) => {
    const colors: Record<string, string> = {
      ACTIVE: "bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-300",
      PAUSED: "bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-300",
      COMPLETED: "bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-300",
    }
    return <Badge className={colors[status] || "bg-gray-100 dark:bg-gray-800"}>{status}</Badge>
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-lg max-h-[80vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Scheduled Reports</DialogTitle>
        </DialogHeader>
        
        <div className="mt-4 space-y-4">
          {loading ? (
            <div className="text-center py-8 text-muted-foreground">Loading...</div>
          ) : scheduledReports.length === 0 ? (
            <div className="text-center py-8 text-muted-foreground">
              No scheduled reports yet.
            </div>
          ) : (
            <div className="rounded-md border">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Name</TableHead>
                    <TableHead>Frequency</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Next Run</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {scheduledReports.map((report) => (
                    <TableRow key={report.id}>
                      <TableCell className="font-medium">{report.name}</TableCell>
                      <TableCell>Daily</TableCell>
                      <TableCell>{getStatusBadge("ACTIVE")}</TableCell>
                      <TableCell className="text-sm text-muted-foreground">
                        {report.generatedAt ? new Date(report.generatedAt).toLocaleDateString() : "—"}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  )
}
