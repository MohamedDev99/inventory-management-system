import { useQuery } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { getProductMovements } from "./api"
import type { PageParams } from "@/types"

export function useProductMovements(productId: number, params: PageParams = {}) {
  return useQuery({
    queryKey: productKeys.movements(productId),
    queryFn: () => getProductMovements(productId, params),
    enabled: !!productId,
  })
}
