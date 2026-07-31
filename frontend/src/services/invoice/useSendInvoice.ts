import { useMutation, useQueryClient } from "@tanstack/react-query"
import { invoiceKeys } from "./keys"
import { sendInvoice } from "./api"
import { toast } from "sonner"

export function useSendInvoice() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => sendInvoice(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: invoiceKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: invoiceKeys.lists() })
      toast.success("Invoice sent successfully")
    },
    onError: () => {
      toast.error("Failed to send invoice")
    },
  })
}
