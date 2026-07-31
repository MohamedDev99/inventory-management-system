import { useMutation, useQueryClient } from "@tanstack/react-query"
import { stockKeys } from "./keys"
import { receiveStock } from "./api"
import type { StockReceiveRequest } from "@/lib/schemas/inventory/request"

export function useReceiveStock() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: StockReceiveRequest) => {
      if (!data.receivedBy) {
        throw new Error("receivedBy is required")
      }
      return receiveStock(data)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: stockKeys.lists() })
    },
  })
}
