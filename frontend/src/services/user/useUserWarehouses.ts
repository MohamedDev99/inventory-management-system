import { useQuery } from "@tanstack/react-query"
import { userKeys } from "./keys"
import { getUserWarehouses } from "./api"

export function useUserWarehouses(userId: number) {
  return useQuery({
    queryKey: userKeys.warehouses(userId),
    queryFn: () => getUserWarehouses(userId),
    enabled: !!userId,
  })
}
