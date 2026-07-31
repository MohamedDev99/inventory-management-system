import { useQuery } from "@tanstack/react-query"
import { productKeys } from "./keys"
import { getProductByBarcode } from "./api"

export function useProductByBarcode(barcode: string) {
  return useQuery({
    queryKey: productKeys.byBarcode(barcode),
    queryFn: () => getProductByBarcode(barcode),
    enabled: !!barcode,
  })
}
