import { useMutation, useQueryClient } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { markNotificationAsUnread } from "./api"

export function useMarkNotificationAsUnread() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => markNotificationAsUnread(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: notificationKeys.lists() })
      queryClient.invalidateQueries({ queryKey: notificationKeys.unread() })
    },
  })
}
