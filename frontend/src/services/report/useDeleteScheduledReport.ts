import { useMutation, useQueryClient } from "@tanstack/react-query"
import { reportKeys } from "./keys"
import { deleteScheduledReport } from "./api"
import { toast } from "sonner"

export function useDeleteScheduledReport() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteScheduledReport(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: reportKeys.scheduled() })
      toast.success("Scheduled report deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete scheduled report")
    },
  })
}
