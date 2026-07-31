import { useQuery } from "@tanstack/react-query"
import { supplierKeys } from "./keys"
import { getSupplier } from "./api"

export function useSupplier(id: number) {
  return useQuery({
    queryKey: supplierKeys.detail(id),
    queryFn: () => getSupplier(id),
    enabled: !!id,
  })
}
