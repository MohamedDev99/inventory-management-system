import { useQuery } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { getNotificationPreferences } from "./api"

export function useNotificationPreferences() {
  return useQuery({
    queryKey: notificationKeys.preferences(),
    queryFn: () => getNotificationPreferences(),
  })
}
