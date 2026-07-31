import { useQuery } from "@tanstack/react-query"
import { warehouseKeys } from "./keys"
import { getWarehouseInventory } from "./api"
import type { PageParams } from "@/types"

export function useWarehouseInventory(warehouseId: number, params: PageParams = {}) {
  return useQuery({
    queryKey: warehouseKeys.inventory(warehouseId),
    queryFn: () => getWarehouseInventory(warehouseId, params),
    enabled: !!warehouseId,
  })
}
