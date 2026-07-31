import { useMutation, useQueryClient } from "@tanstack/react-query"
import { employeeKeys } from "./keys"
import { updateEmployee as updateEmployeeApi } from "./api"
import type { EmployeeFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useUpdateEmployee() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: EmployeeFormData }) =>
      updateEmployeeApi(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: employeeKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: employeeKeys.lists() })
      toast.success("Employee updated successfully")
    },
    onError: () => {
      toast.error("Failed to update employee")
    },
  })
}
