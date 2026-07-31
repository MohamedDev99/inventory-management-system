import { useQuery } from "@tanstack/react-query"
import { paymentKeys } from "./keys"
import { getPaymentMethods } from "./api"

export function usePaymentMethods() {
  return useQuery({
    queryKey: paymentKeys.methods(),
    queryFn: () => getPaymentMethods(),
  })
}
