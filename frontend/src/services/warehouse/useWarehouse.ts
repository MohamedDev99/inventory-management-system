import { useQuery } from "@tanstack/react-query"
import { warehouseKeys } from "./keys"
import { getWarehouse } from "./api"

export function useWarehouse(id: number) {
  return useQuery({
    queryKey: warehouseKeys.detail(id),
    queryFn: () => getWarehouse(id),
    enabled: !!id,
  })
}
