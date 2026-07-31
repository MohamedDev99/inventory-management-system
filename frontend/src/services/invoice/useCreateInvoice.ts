import { useMutation, useQueryClient } from "@tanstack/react-query"
import { invoiceKeys } from "./keys"
import { createInvoice, type CreateInvoiceRequest } from "./api"
import { toast } from "sonner"

export function useCreateInvoice() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateInvoiceRequest) => createInvoice(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: invoiceKeys.lists() })
      toast.success("Invoice created successfully")
    },
    onError: () => {
      toast.error("Failed to create invoice")
    },
  })
}
