import { useMutation, useQueryClient } from "@tanstack/react-query"
import { reportKeys } from "./keys"
import { generateReport, type GenerateReportRequest } from "./api"
import { toast } from "sonner"

export function useGenerateReport() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: GenerateReportRequest) => generateReport(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: reportKeys.lists() })
      toast.success("Report generated successfully")
    },
    onError: () => {
      toast.error("Failed to generate report")
    },
  })
}
