import { useMutation, useQueryClient } from "@tanstack/react-query"
import { stockKeys } from "./keys"
import { createInventoryItem, updateInventoryItem } from "./api"
import type { InventoryItem } from "@/types"
import { toast } from "sonner"

export function useCreateInventoryItem() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: Partial<InventoryItem>) => createInventoryItem(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: stockKeys.lists() })
      toast.success("Stock added successfully")
    },
    onError: () => {
      toast.error("Failed to add stock")
    },
  })
}

export function useUpdateInventoryItem() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ id, data }: { id: number; data: Partial<InventoryItem> }) => 
      updateInventoryItem(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: stockKeys.lists() })
      toast.success("Stock updated successfully")
    },
    onError: () => {
      toast.error("Failed to update stock")
    },
  })
}
