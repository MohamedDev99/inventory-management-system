import { useMutation, useQueryClient } from "@tanstack/react-query"
import { customerKeys } from "./keys"
import { deleteCustomer as deleteCustomerApi } from "./api"
import { toast } from "sonner"

export function useDeleteCustomer() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteCustomerApi(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: customerKeys.lists() })
      toast.success("Customer deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete customer")
    },
  })
}
