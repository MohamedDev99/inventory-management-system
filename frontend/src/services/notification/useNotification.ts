import { useQuery } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { getNotification } from "./api"

export function useNotification(id: number) {
  return useQuery({
    queryKey: notificationKeys.detail(id),
    queryFn: () => getNotification(id),
    enabled: !!id,
  })
}
