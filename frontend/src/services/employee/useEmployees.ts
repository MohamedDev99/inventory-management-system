import { useQuery } from "@tanstack/react-query"
import { employeeKeys } from "./keys"
import { getEmployees } from "./api"
import type { PageParams } from "@/types"

export function useEmployees(params: PageParams = {}) {
  return useQuery({
    queryKey: employeeKeys.list(params),
    queryFn: () => getEmployees(params),
  })
}
