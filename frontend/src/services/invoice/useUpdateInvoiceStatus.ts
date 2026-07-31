import { useMutation, useQueryClient } from "@tanstack/react-query"
import { invoiceKeys } from "./keys"
import { updateInvoiceStatus, type UpdateInvoiceStatusRequest } from "./api"
import { toast } from "sonner"

export function useUpdateInvoiceStatus() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, status }: { id: number; status: UpdateInvoiceStatusRequest["status"] }) =>
      updateInvoiceStatus(id, status),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: invoiceKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: invoiceKeys.lists() })
      toast.success("Invoice status updated")
    },
    onError: () => {
      toast.error("Failed to update invoice status")
    },
  })
}
