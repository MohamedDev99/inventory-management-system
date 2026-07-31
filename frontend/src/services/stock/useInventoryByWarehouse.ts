import { useQuery } from "@tanstack/react-query"
import { stockKeys } from "./keys"
import { getInventoryByWarehouse } from "./api"
import type { PageParams } from "@/types"

export function useInventoryByWarehouse(warehouseId: number, params: PageParams = {}) {
  return useQuery({
    queryKey: [...stockKeys.byWarehouse(warehouseId), params],
    queryFn: () => getInventoryByWarehouse(warehouseId, params),
    enabled: !!warehouseId,
  })
}
