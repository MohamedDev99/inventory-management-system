import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { useLogin as useLoginMutation } from "@/services/auth/useLogin"
import { loginRequestSchema } from "@/lib/schemas/auth/request"
import type { z } from "zod"

type LoginFormData = z.infer<typeof loginRequestSchema>

interface UseLoginReturn {
  form: ReturnType<typeof useForm<LoginFormData>>
  showPassword: boolean
  setShowPassword: (value: boolean) => void
  isLoading: boolean
  onSubmit: (data: LoginFormData) => Promise<void>
}

export function useLogin(): UseLoginReturn {
  const [showPassword, setShowPassword] = useState(false)
  
  // Use React Query mutation hook
  const loginMutation = useLoginMutation()
  
  const form = useForm<LoginFormData>({
    resolver: zodResolver(loginRequestSchema),
    defaultValues: {
      username: "",
      password: "",
    },
    mode: "all",
  })

  const onSubmit = async (data: LoginFormData) => {
    loginMutation.mutate({ 
      username: data.username, 
      password: data.password 
    })
  }

  return {
    form,
    showPassword,
    setShowPassword,
    isLoading: loginMutation.isPending,
    onSubmit,
  }
}
