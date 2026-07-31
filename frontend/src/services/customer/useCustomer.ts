import { useQuery } from "@tanstack/react-query"
import { customerKeys } from "./keys"
import { getCustomer } from "./api"

export function useCustomer(id: number) {
  return useQuery({
    queryKey: customerKeys.detail(id),
    queryFn: () => getCustomer(id),
    enabled: !!id,
  })
}
