import { useQuery } from "@tanstack/react-query"
import { shipmentKeys } from "./keys"
import { getShipmentByTracking } from "./api"

export function useShipmentByTracking(trackingNumber: string) {
  return useQuery({
    queryKey: shipmentKeys.byTracking(trackingNumber),
    queryFn: () => getShipmentByTracking(trackingNumber),
    enabled: !!trackingNumber,
  })
}
