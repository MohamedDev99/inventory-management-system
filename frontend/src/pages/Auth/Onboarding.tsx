import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { ArrowRight, Loader2, Building2, Briefcase, Package } from "lucide-react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Field, FieldLabel, FieldError } from "@/components/ui/field"
import { toast } from "react-hot-toast"
import { onboardingSchema, type OnboardingFormData } from "@/lib/schemas"

const industries = [
  "FMCG",
  "Electronics",
  "Pharmaceuticals",
  "Clothing & Apparel",
  "Food & Beverages",
  "Automotive",
  "Home & Garden",
  "Sports & Outdoors",
]

const domains = [
  "Coffee",
  "Electronics",
  "Fashion",
  "Health & Wellness",
  "Tobacco",
  "Vapes",
  "Snacks",
  "Beverages",
]

const productTypes = [
  "PREMIUM",
  "STANDARD",
  "BUDGET",
  "WHOLESALE",
]

const businessTypes = [
  { value: "super-stockiest", label: "Super-stockiest" },
  { value: "distributor", label: "Distributor" },
  { value: "retailer", label: "Retailer" },
  { value: "brand", label: "Brand" },
]

const skuSizes = [
  { value: "under-500", label: "<500" },
  { value: "501-1000", label: "501-1000" },
  { value: "1001-5000", label: "1001-5000" },
  { value: "5001-10000", label: "5001-10000" },
  { value: "10001-25000", label: "10001-25000" },
  { value: "over-25000", label: ">25000" },
]

export default function Onboarding() {
  const navigate = useNavigate()
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<OnboardingFormData>({
    resolver: zodResolver(onboardingSchema),
    defaultValues: {
      businessName: "",
      industry: "",
      domain: "",
      productType: "",
      businessType: "",
      skuSize: "",
    },
    mode: "all",
  })

  const onSubmit = async (_data: OnboardingFormData) => {
    setIsLoading(true)
    try {
      // Simulated API call
      await new Promise((resolve) => setTimeout(resolve, 1500))
      
      toast.success("Profile setup complete!")
      navigate("/dashboard")
    } catch {
      toast.error("Failed to save profile. Please try again.")
    } finally {
      setIsLoading(false)
    }
  }

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
            <linearGradient id="onboardWaveGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="rgba(255,255,255,0.15)" />
              <stop offset="100%" stopColor="rgba(255,255,255,0)" />
            </linearGradient>
          </defs>
          <circle cx="20" cy="20" r="30" fill="rgba(255,255,255,0.05)" />
          <circle cx="80" cy="80" r="40" fill="rgba(255,255,255,0.05)" />
        </svg>

        <div className="relative z-10 flex flex-col justify-between w-full p-12">
          {/* Logo */}
          <div className="flex items-center gap-3">
            <div className="bg-white/20 backdrop-blur-sm rounded-xl p-3">
              <Package className="w-8 h-8 text-white" />
            </div>
            <span className="font-display text-2xl font-bold text-white">
              MoeWare
            </span>
          </div>

          {/* Brand Message */}
          <div>
            <h2 className="text-3xl font-bold text-white mb-4">
              Engineered to handle all your inventory needs
            </h2>
            <p className="text-primary-100 dark:text-primary-300 text-lg leading-relaxed">
              Set up your business profile to get personalized insights and 
              recommendations for your inventory management.
            </p>

            {/* Features List */}
            <div className="mt-8 space-y-3">
              <div className="flex items-center gap-3 text-white">
                <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
                  <Building2 className="w-4 h-4" />
                </div>
                <span className="text-primary-50 dark:text-primary-200">Multiple warehouse support</span>
              </div>
              <div className="flex items-center gap-3 text-white">
                <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
                  <Briefcase className="w-4 h-4" />
                </div>
                <span className="text-primary-50 dark:text-primary-200">Supplier & customer management</span>
              </div>
              <div className="flex items-center gap-3 text-white">
                <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
                  <Package className="w-4 h-4" />
                </div>
                <span className="text-primary-50 dark:text-primary-200">Real-time inventory tracking</span>
              </div>
            </div>
          </div>

          {/* Copyright */}
          <div>
            <p className="text-primary-200 dark:text-primary-600 text-sm">© TheUnityWare 2024</p>
          </div>
        </div>
      </div>

      {/* Right Panel - Onboarding Form */}
      <div className="w-full lg:w-3/5 bg-accent-50 dark:bg-accent-950 flex items-center justify-center p-6 lg:p-12">
        <div className="w-full max-w-lg">
          {/* Mobile Logo */}
          <div className="flex items-center gap-2 lg:hidden mb-8">
            <div className="bg-primary-500 dark:bg-primary-600 rounded-lg p-2">
              <Package className="w-6 h-6 text-white" />
            </div>
            <span className="font-display text-2xl font-bold text-primary-600 dark:text-primary-400">
              MoeWare
            </span>
          </div>

          {/* Form Header */}
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-accent-900 dark:text-accent-100 mb-2">
              Tell us a little about your business
            </h1>
            <p className="text-accent-600 dark:text-accent-400">
              This helps us personalize your experience
            </p>
          </div>

          {/* Onboarding Form */}
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
            {/* Business Name */}
            <Controller
              name="businessName"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="businessName">Business Name *</FieldLabel>
                  <Input
                    {...field}
                    id="businessName"
                    placeholder="Enter your business name"
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Industry */}
            <Controller
              name="industry"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="industry">Industry *</FieldLabel>
                  <select
                    {...field}
                    id="industry"
                    value={field.value}
                    onChange={field.onChange}
                    className={`flex h-10 w-full rounded-md border border-accent-200 dark:border-accent-700 bg-white dark:bg-accent-800 px-3 py-2 text-sm text-accent-900 dark:text-accent-100 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-accent-950 ${
                      fieldState.invalid ? "border-destructive" : ""
                    }`}
                    aria-invalid={fieldState.invalid}
                  >
                    <option value="">Select Industry</option>
                    {industries.map((industry) => (
                      <option key={industry} value={industry}>
                        {industry}
                      </option>
                    ))}
                  </select>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Domain */}
            <Controller
              name="domain"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="domain">Domain *</FieldLabel>
                  <select
                    {...field}
                    id="domain"
                    value={field.value}
                    onChange={field.onChange}
                    className={`flex h-10 w-full rounded-md border border-accent-200 dark:border-accent-700 bg-white dark:bg-accent-800 px-3 py-2 text-sm text-accent-900 dark:text-accent-100 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-accent-950 ${
                      fieldState.invalid ? "border-destructive" : ""
                    }`}
                    aria-invalid={fieldState.invalid}
                  >
                    <option value="">Select Domain</option>
                    {domains.map((domain) => (
                      <option key={domain} value={domain}>
                        {domain}
                      </option>
                    ))}
                  </select>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Product Type */}
            <Controller
              name="productType"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="productType">Product/Services Offered *</FieldLabel>
                  <select
                    {...field}
                    id="productType"
                    value={field.value}
                    onChange={field.onChange}
                    className={`flex h-10 w-full rounded-md border border-accent-200 dark:border-accent-700 bg-white dark:bg-accent-800 px-3 py-2 text-sm text-accent-900 dark:text-accent-100 focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2 dark:focus:ring-offset-accent-950 ${
                      fieldState.invalid ? "border-destructive" : ""
                    }`}
                    aria-invalid={fieldState.invalid}
                  >
                    <option value="">Select Product Type</option>
                    {productTypes.map((type) => (
                      <option key={type} value={type}>
                        {type}
                      </option>
                    ))}
                  </select>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Business Type */}
            <Controller
              name="businessType"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel>Which of the following best describes you? *</FieldLabel>
                  <div className="grid grid-cols-2 gap-3">
                    {businessTypes.map((type) => (
                      <label
                        key={type.value}
                        className={`flex items-center justify-center p-3 rounded-lg border cursor-pointer transition-colors ${
                          field.value === type.value
                            ? "border-primary-500 bg-primary-50 dark:bg-primary-900/30 text-primary-700 dark:text-primary-300"
                            : "border-accent-200 dark:border-accent-700 hover:border-primary-300 dark:hover:border-primary-600 bg-white dark:bg-accent-800 text-accent-900 dark:text-accent-100"
                        }`}
                      >
                        <input
                          type="radio"
                          name={field.name}
                          value={type.value}
                          checked={field.value === type.value}
                          onChange={(e) => field.onChange(e.target.value)}
                          className="sr-only"
                        />
                        <span className="text-sm font-medium">{type.label}</span>
                      </label>
                    ))}
                  </div>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* SKU Size */}
            <Controller
              name="skuSize"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel>Storage Keeping Unit (SKU) Size *</FieldLabel>
                  <div className="grid grid-cols-3 gap-2">
                    {skuSizes.map((size) => (
                      <label
                        key={size.value}
                        className={`flex items-center justify-center p-3 rounded-lg border cursor-pointer transition-colors ${
                          field.value === size.value
                            ? "border-primary-500 bg-primary-50 dark:bg-primary-900/30 text-primary-700 dark:text-primary-300"
                            : "border-accent-200 dark:border-accent-700 hover:border-primary-300 dark:hover:border-primary-600 bg-white dark:bg-accent-800 text-accent-900 dark:text-accent-100"
                        }`}
                      >
                        <input
                          type="radio"
                          name={field.name}
                          value={size.value}
                          checked={field.value === size.value}
                          onChange={(e) => field.onChange(e.target.value)}
                          className="sr-only"
                        />
                        <span className="text-sm font-medium">{size.label}</span>
                      </label>
                    ))}
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
                  Setting up...
                </>
              ) : (
                <>
                  Get Started
                  <ArrowRight className="w-4 h-4 ml-2" />
                </>
              )}
            </Button>
          </form>
        </div>
      </div>
    </div>
  )
}
