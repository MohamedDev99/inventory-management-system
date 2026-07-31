import { useQuery } from "@tanstack/react-query"
import { departmentKeys } from "./keys"
import { getDepartment } from "./api"

export function useDepartment(id: number) {
  return useQuery({
    queryKey: departmentKeys.detail(id),
    queryFn: () => getDepartment(id),
    enabled: !!id,
  })
}
