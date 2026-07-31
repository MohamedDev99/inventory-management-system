import { useMutation, useQueryClient } from "@tanstack/react-query"
import { reportKeys } from "./keys"
import { scheduleReport, type ScheduleReportRequest } from "./api"
import { toast } from "sonner"

export function useScheduleReport() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: ScheduleReportRequest) => scheduleReport(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: reportKeys.scheduled() })
      toast.success("Report scheduled successfully")
    },
    onError: () => {
      toast.error("Failed to schedule report")
    },
  })
}
