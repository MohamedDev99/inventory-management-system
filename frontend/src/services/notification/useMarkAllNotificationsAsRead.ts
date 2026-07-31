import { useMutation, useQueryClient } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { markAllNotificationsAsRead } from "./api"
import { toast } from "sonner"

export function useMarkAllNotificationsAsRead() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: () => markAllNotificationsAsRead(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.lists() })
      queryClient.invalidateQueries({ queryKey: notificationKeys.unread() })
      toast.success("All notifications marked as read")
    },
    onError: () => {
      toast.error("Failed to mark all notifications as read")
    },
  })
}
