import { useQuery } from "@tanstack/react-query"
import { shipmentKeys } from "./keys"
import { getShipment } from "./api"

export function useShipment(id: number) {
  return useQuery({
    queryKey: shipmentKeys.detail(id),
    queryFn: () => getShipment(id),
    enabled: !!id,
  })
}
