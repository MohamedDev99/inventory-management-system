import { useQuery } from "@tanstack/react-query"
import { stockKeys } from "./keys"
import { getInventory } from "./api"
import type { PageParams } from "@/types"

export function useInventory(params: PageParams = {}) {
  return useQuery({
    queryKey: stockKeys.list(params),
    queryFn: () => getInventory(params),
  })
}
