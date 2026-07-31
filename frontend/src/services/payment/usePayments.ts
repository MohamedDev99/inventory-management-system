import { useQuery } from "@tanstack/react-query"
import { paymentKeys } from "./keys"
import { getPayments } from "./api"
import type { PaymentParams } from "./api"

export function usePayments(params: PaymentParams = {}) {
  return useQuery({
    queryKey: paymentKeys.list(params),
    queryFn: () => getPayments(params),
  })
}
