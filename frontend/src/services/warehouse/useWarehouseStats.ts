import { useQuery } from "@tanstack/react-query"
import { warehouseKeys } from "./keys"
import { getWarehouseStats } from "./api"

export function useWarehouseStats(warehouseId: number) {
  return useQuery({
    queryKey: warehouseKeys.stats(warehouseId),
    queryFn: () => getWarehouseStats(warehouseId),
    enabled: !!warehouseId,
  })
}
