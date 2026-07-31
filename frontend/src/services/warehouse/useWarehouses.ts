import { useQuery } from "@tanstack/react-query"
import { warehouseKeys } from "./keys"
import { getWarehouses } from "./api"
import type { PageParams } from "@/types"

export function useWarehouses(params: PageParams = {}) {
  return useQuery({
    queryKey: warehouseKeys.list(params),
    queryFn: () => getWarehouses(params),
  })
}
