import { useMutation, useQueryClient } from "@tanstack/react-query"
import { customerKeys } from "./keys"
import { createCustomer as createCustomerApi } from "./api"
import type { CustomerFormData } from "@/lib/schemas"
import { toast } from "sonner"

export function useCreateCustomer() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CustomerFormData) => createCustomerApi(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: customerKeys.lists() })
      toast.success("Customer added successfully")
    },
    onError: () => {
      toast.error("Failed to add customer")
    },
  })
}
