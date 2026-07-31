import { useQuery } from "@tanstack/react-query"
import { userKeys } from "./keys"
import { getUser } from "./api"

export function useUser(id: number) {
  return useQuery({
    queryKey: userKeys.detail(id),
    queryFn: () => getUser(id),
    enabled: !!id,
  })
}
