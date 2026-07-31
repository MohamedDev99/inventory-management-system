import { useMutation, useQueryClient } from "@tanstack/react-query"
import { departmentKeys } from "./keys"
import { updateDepartment as updateDepartmentApi } from "./api"
import type { DepartmentFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useUpdateDepartment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: DepartmentFormData }) =>
      updateDepartmentApi(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: departmentKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: departmentKeys.lists() })
      toast.success("Department updated successfully")
    },
    onError: () => {
      toast.error("Failed to update department")
    },
  })
}
