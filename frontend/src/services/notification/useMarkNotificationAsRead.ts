import { useMutation, useQueryClient } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { markNotificationAsRead } from "./api"
import { toast } from "sonner"

export function useMarkNotificationAsRead() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => markNotificationAsRead(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: notificationKeys.lists() })
      queryClient.invalidateQueries({ queryKey: notificationKeys.unread() })
      toast.success("Notification marked as read")
    },
    onError: () => {
      toast.error("Failed to mark notification as read")
    },
  })
}
