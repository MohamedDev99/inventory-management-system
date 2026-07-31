import { useMutation, useQueryClient } from "@tanstack/react-query"
import { shipmentKeys } from "./keys"
import { deliverShipment } from "./api"
import { toast } from "sonner"

export type DeliverShipmentData = {
  actualDeliveryDate?: string
  receivedBy?: string
  notes?: string
}

export function useDeliverShipment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data?: DeliverShipmentData }) =>
      deliverShipment(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: shipmentKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: shipmentKeys.lists() })
      toast.success("Shipment delivered successfully")
    },
    onError: () => {
      toast.error("Failed to deliver shipment")
    },
  })
}
