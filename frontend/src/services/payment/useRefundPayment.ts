import { useMutation, useQueryClient } from "@tanstack/react-query"
import { paymentKeys } from "./keys"
import { refundPayment, type RefundPaymentRequest } from "./api"
import { toast } from "sonner"

export function useRefundPayment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: RefundPaymentRequest }) =>
      refundPayment(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: paymentKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: paymentKeys.lists() })
      toast.success("Payment refunded successfully")
    },
    onError: () => {
      toast.error("Failed to refund payment")
    },
  })
}
