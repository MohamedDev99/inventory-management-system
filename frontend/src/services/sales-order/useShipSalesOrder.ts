import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { shipSalesOrder } from "./api"
import { toast } from "sonner"

export interface ShipSalesOrderData {
  shippingDate?: string
  carrier?: string
  trackingNumber?: string
}

export function useShipSalesOrder() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data?: ShipSalesOrderData }) =>
      shipSalesOrder(id, data),
    onSuccess: (_, { id }) => {
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.detail(id) })
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.lists() })
      toast.success("Sales order shipped")
    },
    onError: () => {
      toast.error("Failed to ship sales order")
    },
  })
}
