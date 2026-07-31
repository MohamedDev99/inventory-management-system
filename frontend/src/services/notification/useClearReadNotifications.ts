import { useMutation, useQueryClient } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { clearReadNotifications } from "./api"
import { toast } from "sonner"

export function useClearReadNotifications() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: () => clearReadNotifications(),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.lists() })
      queryClient.invalidateQueries({ queryKey: notificationKeys.unread() })
      toast.success("Read notifications cleared successfully")
    },
    onError: () => {
      toast.error("Failed to clear read notifications")
    },
  })
}
