import { useQuery } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { getLowStockProducts } from "./api"

export function useLowStockProducts() {
  return useQuery({
    queryKey: productKeys.lowStock(),
    queryFn: () => getLowStockProducts(),
  })
}
