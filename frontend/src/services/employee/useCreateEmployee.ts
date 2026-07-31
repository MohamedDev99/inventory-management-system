import { useMutation, useQueryClient } from "@tanstack/react-query"
import { employeeKeys } from "./keys"
import { createEmployee as createEmployeeApi } from "./api"
import type { EmployeeFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useCreateEmployee() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: EmployeeFormData) => createEmployeeApi(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: employeeKeys.lists() })
      toast.success("Employee added successfully")
    },
    onError: () => {
      toast.error("Failed to add employee")
    },
  })
}
