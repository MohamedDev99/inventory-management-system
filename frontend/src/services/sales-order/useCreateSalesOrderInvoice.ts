import { useMutation, useQueryClient } from "@tanstack/react-query"
import { salesOrderKeys } from "./keys"
import { createSalesOrderInvoice } from "./api"

export function useCreateSalesOrderInvoice() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => createSalesOrderInvoice(id),
    onSuccess: (_, id) => {
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.invoice(id) })
      queryClient.invalidateQueries({ queryKey: salesOrderKeys.detail(id) })
    },
  })
}
