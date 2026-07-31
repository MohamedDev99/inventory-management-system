import { useQuery } from "@tanstack/react-query"
import { userKeys } from "./keys"
import { getCurrentUser } from "@/services/auth/api"

export function useCurrentUser() {
  return useQuery({
    queryKey: userKeys.me(),
    queryFn: () => getCurrentUser(),
  })
}
