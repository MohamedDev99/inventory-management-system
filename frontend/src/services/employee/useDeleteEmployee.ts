import { useMutation, useQueryClient } from "@tanstack/react-query"
import { employeeKeys } from "./keys"
import { deleteEmployee as deleteEmployeeApi } from "./api"
import { toast } from "sonner"

export function useDeleteEmployee() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteEmployeeApi(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: employeeKeys.lists() })
      toast.success("Employee deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete employee")
    },
  })
}
