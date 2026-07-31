import { useQuery } from "@tanstack/react-query"
import { paymentKeys } from "./keys"
import { getPayment } from "./api"

export function usePayment(id: number) {
  return useQuery({
    queryKey: paymentKeys.detail(id),
    queryFn: () => getPayment(id),
    enabled: !!id,
  })
}
