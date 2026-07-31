import { useMutation, useQueryClient } from "@tanstack/react-query"
import { invoiceKeys } from "./keys"
import { recordInvoicePayment, type RecordInvoicePaymentRequest } from "./api"
import { toast } from "sonner"

export function useRecordInvoicePayment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: RecordInvoicePaymentRequest }) =>
      recordInvoicePayment(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: invoiceKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: invoiceKeys.lists() })
      toast.success("Payment recorded successfully")
    },
    onError: () => {
      toast.error("Failed to record payment")
    },
  })
}
