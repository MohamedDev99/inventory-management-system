import { useQuery } from "@tanstack/react-query"
import { purchaseOrderKeys } from "./keys"
import { getPurchaseOrderPdf } from "./api"

export function usePurchaseOrderPdf(id: number) {
  return useQuery({
    queryKey: [...purchaseOrderKeys.detail(id), 'pdf'],
    queryFn: () => getPurchaseOrderPdf(id),
    enabled: !!id,
  })
}
