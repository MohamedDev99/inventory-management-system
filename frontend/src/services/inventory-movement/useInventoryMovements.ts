import { useQuery } from "@tanstack/react-query"
import { inventoryMovementKeys } from "./keys"
import { getInventoryMovements } from "./api"
import type { InventoryMovementParams } from "./api"

export function useInventoryMovements(params: InventoryMovementParams = {}) {
  return useQuery({
    queryKey: inventoryMovementKeys.list(params),
    queryFn: () => getInventoryMovements(params),
  })
}
