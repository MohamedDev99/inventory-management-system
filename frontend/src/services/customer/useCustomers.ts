import { useQuery } from "@tanstack/react-query"
import { customerKeys } from "./keys"
import { getCustomers } from "./api"
import type { PageParams } from "@/types"

export function useCustomers(params: PageParams = {}) {
  return useQuery({
    queryKey: customerKeys.list(params),
    queryFn: () => getCustomers(params),
  })
}
