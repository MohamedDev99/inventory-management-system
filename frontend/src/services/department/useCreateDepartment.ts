import { useMutation, useQueryClient } from "@tanstack/react-query"
import { departmentKeys } from "./keys"
import { createDepartment as createDepartmentApi } from "./api"
import type { DepartmentFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useCreateDepartment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: DepartmentFormData) => createDepartmentApi(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: departmentKeys.lists() })
      toast.success("Department added successfully")
    },
    onError: () => {
      toast.error("Failed to add department")
    },
  })
}
