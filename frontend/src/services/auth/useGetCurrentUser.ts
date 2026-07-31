import { useQuery } from "@tanstack/react-query"
import { authKeys } from "./keys"
import { getCurrentUser as getCurrentUserApi } from "./api"

export function useGetCurrentUser() {
  return useQuery({
    queryKey: authKeys.currentUser(),
    queryFn: () => getCurrentUserApi(),
  })
}
