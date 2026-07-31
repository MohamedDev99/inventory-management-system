import { useQuery } from "@tanstack/react-query"
import { shipmentKeys } from "./keys"
import { getShipmentTracking } from "./api"

export function useShipmentTracking(id: number) {
  return useQuery({
    queryKey: shipmentKeys.tracking(id),
    queryFn: () => getShipmentTracking(id),
    enabled: !!id,
  })
}
