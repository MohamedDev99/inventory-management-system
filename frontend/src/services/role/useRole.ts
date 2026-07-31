import { useQuery } from "@tanstack/react-query"
import { roleKeys } from "./keys"
import { getRole } from "./api"

export function useRole(id: number) {
  return useQuery({
    queryKey: roleKeys.detail(id),
    queryFn: () => getRole(id),
    enabled: !!id,
  })
}
