import { useQuery } from "@tanstack/react-query"
import { stockKeys } from "./keys"
import { getInventoryItem } from "./api"

export function useInventoryItem(id: number) {
  return useQuery({
    queryKey: stockKeys.detail(id),
    queryFn: () => getInventoryItem(id),
    enabled: !!id,
  })
}
