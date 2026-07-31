import { useMutation, useQueryClient } from "@tanstack/react-query"
import { paymentKeys } from "./keys"
import { updatePaymentStatus, type UpdatePaymentStatusRequest } from "./api"
import { toast } from "sonner"

export function useUpdatePaymentStatus() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, status, notes }: { id: number; status: UpdatePaymentStatusRequest["status"]; notes?: string }) =>
      updatePaymentStatus(id, status, notes),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: paymentKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: paymentKeys.lists() })
      toast.success("Payment status updated")
    },
    onError: () => {
      toast.error("Failed to update payment status")
    },
  })
}
