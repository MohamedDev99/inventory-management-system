import { useMutation, useQueryClient } from "@tanstack/react-query"
import { shipmentKeys } from "./keys"
import { createShipment, type CreateShipmentRequest } from "./api"
import { toast } from "sonner"

export function useCreateShipment() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CreateShipmentRequest) => createShipment(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: shipmentKeys.lists() })
      toast.success("Shipment created successfully")
    },
    onError: () => {
      toast.error("Failed to create shipment")
    },
  })
}
