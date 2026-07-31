import { useQuery } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { getNotifications } from "./api"
import type { NotificationParams } from "./api"

export function useNotifications(params: NotificationParams = {}) {
  return useQuery({
    queryKey: notificationKeys.list(params),
    queryFn: () => getNotifications(params),
  })
}
