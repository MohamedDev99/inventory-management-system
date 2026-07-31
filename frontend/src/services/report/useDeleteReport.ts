import { useMutation, useQueryClient } from "@tanstack/react-query"
import { reportKeys } from "./keys"
import { deleteReport } from "./api"
import { toast } from "sonner"

export function useDeleteReport() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteReport(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: reportKeys.lists() })
      toast.success("Report deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete report")
    },
  })
}
