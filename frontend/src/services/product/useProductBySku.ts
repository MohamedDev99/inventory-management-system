import { useQuery } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { getProductBySku } from "./api"

export function useProductBySku(sku: string) {
  return useQuery({
    queryKey: productKeys.bySku(sku),
    queryFn: () => getProductBySku(sku),
    enabled: !!sku,
  })
}
