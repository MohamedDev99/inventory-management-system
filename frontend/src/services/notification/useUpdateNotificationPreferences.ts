import { useMutation, useQueryClient } from "@tanstack/react-query"
import { notificationKeys } from "./keys"
import { updateNotificationPreferences } from "./api"
import type { NotificationPreferences } from "@/types"
import { toast } from "sonner"

export function useUpdateNotificationPreferences() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: NotificationPreferences) => updateNotificationPreferences(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: notificationKeys.preferences() })
      toast.success("Notification preferences saved")
    },
    onError: () => {
      toast.error("Failed to save preferences")
    },
  })
}
