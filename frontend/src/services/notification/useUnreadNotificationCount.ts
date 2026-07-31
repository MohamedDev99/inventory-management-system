import { useQuery } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { getUnreadNotificationCount } from "./api"

export function useUnreadNotificationCount() {
  return useQuery({
    queryKey: notificationKeys.unread(),
    queryFn: () => getUnreadNotificationCount(),
    refetchInterval: 60000, // Poll every 60 seconds
  })
}
