import { useQuery } from "@tanstack/react-query"
import { departmentKeys } from "./keys"
import { getDepartments } from "./api"
import type { PageParams } from "@/types"

export function useDepartments(params: PageParams = {}) {
  return useQuery({
    queryKey: departmentKeys.list(params),
    queryFn: () => getDepartments(params),
  })
}
