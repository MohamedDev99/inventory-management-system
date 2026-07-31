import { useMutation, useQueryClient } from "@tanstack/react-query"
import { stockKeys } from "./keys"
import { transferStock } from "./api"
import type { StockTransferRequest } from "@/lib/schemas/inventory/request"

export function useTransferStock() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: StockTransferRequest) => {
      if (!data.performedBy) {
        throw new Error("performedBy is required")
      }
      return transferStock(data)
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: stockKeys.lists() })
    },
  })
}
