import { useQuery } from "@tanstack/react-query"
import { inventoryMovementKeys } from "./keys"
import { getInventoryMovement } from "./api"

export function useInventoryMovement(id: number) {
  return useQuery({
    queryKey: inventoryMovementKeys.detail(id),
    queryFn: () => getInventoryMovement(id),
    enabled: !!id,
  })
}
