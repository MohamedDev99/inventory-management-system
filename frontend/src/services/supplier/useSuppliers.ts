import { useQuery } from "@tanstack/react-query"
import { supplierKeys } from "./keys"
import { getSuppliers } from "./api"
import type { PageParams } from "@/types"

export function useSuppliers(params: PageParams = {}) {
  return useQuery({
    queryKey: supplierKeys.list(params),
    queryFn: () => getSuppliers(params),
  })
}
