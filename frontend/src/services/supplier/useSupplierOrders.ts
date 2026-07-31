import { useQuery } from "@tanstack/react-query"
import { supplierKeys } from "./keys"
import { getSupplierOrders } from "./api"
import type { PageParams } from "@/types"

export function useSupplierOrders(supplierId: number, params: PageParams = {}) {
  return useQuery({
    queryKey: supplierKeys.orders(supplierId),
    queryFn: () => getSupplierOrders(supplierId, params),
    enabled: !!supplierId,
  })
}
