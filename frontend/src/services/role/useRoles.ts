import { useQuery } from "@tanstack/react-query"
import { roleKeys } from "./keys"
import { getRoles } from "./api"

export function useRoles() {
  return useQuery({
    queryKey: roleKeys.list(),
    queryFn: () => getRoles(),
  })
}
