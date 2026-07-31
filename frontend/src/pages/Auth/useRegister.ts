import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { useRegister as useRegisterMutation } from "@/services/auth/useRegister"
import { registerRequestSchema } from "@/lib/schemas/auth/request"
import type { z } from "zod"

type RegisterFormData = z.infer<typeof registerRequestSchema>

interface UseRegisterReturn {
  form: ReturnType<typeof useForm<RegisterFormData>>
  isLoading: boolean
  onSubmit: (data: RegisterFormData) => Promise<void>
}

export function useRegister(): UseRegisterReturn {
  // Use React Query mutation hook
  const registerMutation = useRegisterMutation()
  
  const form = useForm<RegisterFormData>({
    resolver: zodResolver(registerRequestSchema),
    defaultValues: {
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
      roleName: "MANAGER",
    },
    mode: "all",
  })

  const onSubmit = async (data: RegisterFormData) => {
    registerMutation.mutate({
      username: data.username,
      email: data.email,
      password: data.password,
      roleName: data.roleName,
    })
  }

  return {
    form,
    isLoading: registerMutation.isPending,
    onSubmit,
  }
}
