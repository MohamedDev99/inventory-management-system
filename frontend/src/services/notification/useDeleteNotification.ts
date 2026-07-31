import { useMutation, useQueryClient } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { deleteNotification } from "./api"
import { toast } from "sonner"

export function useDeleteNotification() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => deleteNotification(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.lists() })
      queryClient.invalidateQueries({ queryKey: notificationKeys.unread() })
      toast.success("Notification deleted successfully")
    },
    onError: () => {
      toast.error("Failed to delete notification")
    },
  })
}
