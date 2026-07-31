import { useQuery } from "@tanstack/react-query"
import { supplierKeys } from "./keys"
import { getSupplierPerformance } from "./api"

export function useSupplierPerformance(supplierId: number) {
  return useQuery({
    queryKey: supplierKeys.performance(supplierId),
    queryFn: () => getSupplierPerformance(supplierId),
    enabled: !!supplierId,
  })
}
