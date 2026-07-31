import { useQuery } from "@tanstack/react-query"
import { inventoryMovementKeys } from "./keys"
import { getInventoryMovementsByWarehouse } from "./api"

export function useInventoryMovementsByWarehouse(warehouseId: number, params: { direction?: "IN" | "OUT" | "BOTH" } = {}) {
  return useQuery({
    queryKey: [...inventoryMovementKeys.byWarehouse(warehouseId), params],
    queryFn: () => getInventoryMovementsByWarehouse(warehouseId, params),
    enabled: !!warehouseId,
  })
}
