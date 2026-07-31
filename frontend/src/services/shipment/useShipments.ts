import { useQuery } from "@tanstack/react-query"
import { shipmentKeys } from "./keys"
import { getShipments } from "./api"
import type { ShipmentParams } from "./api"

export function useShipments(params: ShipmentParams = {}) {
  return useQuery({
    queryKey: shipmentKeys.list(params),
    queryFn: () => getShipments(params),
  })
}
