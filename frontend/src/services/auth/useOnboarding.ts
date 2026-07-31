import { useMutation } from "@tanstack/react-query"
import { onboarding as onboardingApi } from "./api"

export function useOnboarding() {
  return useMutation({
    mutationFn: (data: {
      businessName: string
      industry: string
      domain: string
      productType: string
      businessType: string
      skuSize: string
    }) => onboardingApi(data),
  })
}
