import { useMutation, useQueryClient } from "@tanstack/react-query"
import { paymentKeys } from "./keys"
import { createPayment, type CreatePaymentRequest } from "./api"
import { toast } from "sonner"

export function useCreatePayment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreatePaymentRequest) => createPayment(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: paymentKeys.lists() })
      toast.success("Payment recorded successfully")
    },
    onError: () => {
      toast.error("Failed to record payment")
    },
  })
}
