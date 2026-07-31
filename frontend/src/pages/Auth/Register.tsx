import { Link } from "react-router-dom"
import { Mail, Lock, Loader2, User, Briefcase } from "lucide-react"
import { Controller } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Field, FieldLabel, FieldError } from "@/components/ui/field"
import { useRegister } from "./useRegister"

const roles = [
  { value: "ADMIN", label: "Administrator" },
  { value: "MANAGER", label: "Manager" },
  { value: "WAREHOUSE_STAFF", label: "Warehouse Staff" },
  { value: "VIEWER", label: "Viewer" },
]

export default function Register() {
  const { form, isLoading, onSubmit } = useRegister()

  return (
    <div className="min-h-screen flex">
      {/* Left Panel - Brand */}
      <div className="hidden lg:flex lg:w-2/5 bg-gradient-to-br from-primary-500 via-primary-600 to-primary-700 relative overflow-hidden">
        {/* Background Pattern */}
        <svg
          className="absolute inset-0 w-full h-full"
          viewBox="0 0 100 100"
          preserveAspectRatio="none"
        >
          <defs>
            <linearGradient id="registerWaveGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="rgba(255,255,255,0.15)" />
              <stop offset="100%" stopColor="rgba(255,255,255,0)" />
            </linearGradient>
          </defs>
          <circle cx="20" cy="20" r="30" fill="rgba(255,255,255,0.05)" />
          <circle cx="80" cy="80" r="40" fill="rgba(255,255,255,0.05)" />
          <circle cx="50" cy="50" r="20" fill="rgba(255,255,255,0.03)" />
        </svg>

        <div className="relative z-10 flex flex-col justify-between w-full p-12">
          {/* Logo */}
          <div className="flex items-center gap-3">
            <div className="bg-white/20 backdrop-blur-sm rounded-xl p-3">
              <svg
                className="w-8 h-8 text-white"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
                />
              </svg>
            </div>
            <span className="font-display text-2xl font-bold text-white">
              MoeWare
            </span>
          </div>

          {/* Brand Message */}
          <div>
            <h2 className="text-3xl font-bold text-white mb-4">
              Re-imagining inventory management
            </h2>
            <p className="text-primary-100 dark:text-primary-300 text-lg leading-relaxed">
              Experience advanced data analytics for optimum performance. 
              Streamline your operations with intelligent insights.
            </p>
          </div>

          {/* Copyright */}
          <div>
            <p className="text-primary-200 dark:text-primary-600 text-sm">
              © TheUnityWare 2024
            </p>
          </div>
        </div>
      </div>

      {/* Right Panel - Register Form */}
      <div className="w-full lg:w-3/5 bg-accent-50 dark:bg-accent-950 flex items-center justify-center p-6 lg:p-12">
        <div className="w-full max-w-md">
          {/* Mobile Logo */}
          <div className="flex items-center gap-2 lg:hidden mb-8">
            <div className="bg-primary-500 dark:bg-primary-600 rounded-lg p-2">
              <svg
                className="w-6 h-6 text-white"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"
                />
              </svg>
            </div>
            <span className="font-display text-2xl font-bold text-primary-600 dark:text-primary-400">
              MoeWare
            </span>
          </div>

          {/* Form Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-accent-900 dark:text-accent-100 mb-2">
              Create your account
            </h1>
            <p className="text-accent-600 dark:text-accent-400">
              Enter your details to get started
            </p>
          </div>

          {/* Register Form */}
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-5">
            {/* Username */}
            <Controller
              name="username"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="username">Username</FieldLabel>
                  <div className="relative">
                    <User className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 stroke-accent-500 dark:stroke-accent-400 z-10 pointer-events-none" />
                    <Input
                      {...field}
                      id="username"
                      type="text"
                      placeholder="Choose a username"
                      className="pl-10"
                      aria-invalid={fieldState.invalid}
                    />
                  </div>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Email */}
            <Controller
              name="email"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="email">Email Address</FieldLabel>
                  <div className="relative">
                    <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 stroke-accent-500 dark:stroke-accent-400 z-10 pointer-events-none" />
                    <Input
                      {...field}
                      id="email"
                      type="email"
                      placeholder="name@company.com"
                      className="pl-10"
                      aria-invalid={fieldState.invalid}
                    />
                  </div>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Password */}
            <Controller
              name="password"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="password">Password</FieldLabel>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 stroke-accent-500 dark:stroke-accent-400 z-10 pointer-events-none" />
                    <Input
                      {...field}
                      id="password"
                      type="password"
                      placeholder="Create a password"
                      className="pl-10 pr-10"
                      aria-invalid={fieldState.invalid}
                    />
                  </div>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Confirm Password */}
            <Controller
              name="confirmPassword"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="confirmPassword">Confirm Password</FieldLabel>
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 stroke-accent-500 dark:stroke-accent-400 z-10 pointer-events-none" />
                    <Input
                      {...field}
                      id="confirmPassword"
                      type="password"
                      placeholder="Confirm your password"
                      className="pl-10 pr-10"
                      aria-invalid={fieldState.invalid}
                    />
                  </div>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Role */}
            <Controller
              name="roleName"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="roleName">Role</FieldLabel>
                  <div className="relative">
                    <Briefcase className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 stroke-accent-500 dark:stroke-accent-400 z-10 pointer-events-none" />
                    <select
                      {...field}
                      id="roleName"
                      value={field.value}
                      onChange={field.onChange}
                      className="flex h-10 w-full rounded-md border border-accent-200 dark:border-accent-700 bg-white dark:bg-accent-800 px-3 py-2 pl-10 text-sm text-accent-900 dark:text-accent-100 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-accent-950"
                    >
                      {roles.map((role) => (
                        <option key={role.value} value={role.value}>
                          {role.label}
                        </option>
                      ))}
                    </select>
                  </div>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Submit Button */}
            <Button
              type="submit"
              className="w-full bg-primary-500 hover:bg-primary-600 dark:bg-primary-500 dark:hover:bg-primary-400"
              disabled={isLoading}
            >
              {isLoading ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Creating account...
                </>
              ) : (
                "Create account"
              )}
            </Button>

            {/* Sign In Link */}
            <p className="text-center text-sm text-accent-600 dark:text-accent-400">
              Already have an account?{" "}
              <Link to="/login" className="text-primary-500 hover:text-primary-600 dark:text-primary-400 dark:hover:text-primary-300 font-medium">
                Sign in
              </Link>
            </p>
          </form>
        </div>
      </div>
    </div>
  )
}
