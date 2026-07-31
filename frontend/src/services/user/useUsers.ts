import { useQuery } from "@tanstack/react-query"
import { userKeys } from "./keys"
import { getUsers } from "./api"
import type { UserParams } from "./api"

export function useUsers(params: UserParams = {}) {
  return useQuery({
    queryKey: userKeys.list(params),
    queryFn: () => getUsers(params),
  })
}
