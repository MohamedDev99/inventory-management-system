import { useMutation, useQueryClient } from "@tanstack/react-query"
import { customerKeys } from "./keys"
import { updateCustomer as updateCustomerApi } from "./api"
import type { UpdateCustomerRequest } from "./request"
import { toast } from "sonner"

export function useUpdateCustomer() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateCustomerRequest }) =>
      updateCustomerApi(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: customerKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: customerKeys.lists() })
      toast.success("Customer updated successfully")
    },
    onError: () => {
      toast.error("Failed to update customer")
    },
  })
}
