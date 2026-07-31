import { useMutation, useQueryClient } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { uploadProductImage as uploadProductImageApi } from "./api"

export function useUploadProductImage() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ productId, formData }: { productId: number; formData: FormData }) =>
      uploadProductImageApi(productId, formData),
    onSuccess: (_, { productId }) => {
      queryClient.invalidateQueries({ queryKey: productKeys.detail(productId) })
    },
  })
}
