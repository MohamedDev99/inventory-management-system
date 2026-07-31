import { useMutation } from "@tanstack/react-query"
import { refreshToken as refreshTokenApi } from "./api"

export function useRefreshToken() {
  return useMutation({
    mutationFn: (refreshToken: string) => refreshTokenApi(refreshToken),
  })
}
