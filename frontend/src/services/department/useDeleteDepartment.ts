import { useMutation, useQueryClient } from "@tanstack/react-query"
import { departmentKeys } from "./keys"
import { deleteDepartment as deleteDepartmentApi } from "./api"
import { toast } from "sonner"

export function useDeleteDepartment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteDepartmentApi(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: departmentKeys.lists() })
      toast.success("Department deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete department")
    },
  })
}
