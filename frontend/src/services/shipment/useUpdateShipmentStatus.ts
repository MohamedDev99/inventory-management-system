import { useMutation, useQueryClient } from "@tanstack/react-query"
import { shipmentKeys } from "./keys"
import { updateShipmentStatus } from "./api"
import { toast } from "sonner"

export type UpdateShipmentStatusData = {
  status: "PENDING" | "IN_TRANSIT" | "DELIVERED" | "CANCELLED"
  location?: string
}

export function useUpdateShipmentStatus() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdateShipmentStatusData }) =>
      updateShipmentStatus(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: shipmentKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: shipmentKeys.lists() })
      toast.success("Shipment status updated")
    },
    onError: () => {
      toast.error("Failed to update shipment status")
    },
  })
}
