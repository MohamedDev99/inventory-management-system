import { useQuery } from "@tanstack/react-query"
import { inventoryMovementKeys } from "./keys"
import { getInventoryMovementsByProduct } from "./api"
import type { PageParams } from "@/types"

export function useInventoryMovementsByProduct(productId: number, params: PageParams = {}) {
  return useQuery({
    queryKey: [...inventoryMovementKeys.byProduct(productId), params],
    queryFn: () => getInventoryMovementsByProduct(productId, params),
    enabled: !!productId,
  })
}
