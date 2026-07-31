import { useQuery } from "@tanstack/react-query"
import { employeeKeys } from "./keys"
import { getEmployee } from "./api"

export function useEmployee(id: number) {
  return useQuery({
    queryKey: employeeKeys.detail(id),
    queryFn: () => getEmployee(id),
    enabled: !!id,
  })
}
