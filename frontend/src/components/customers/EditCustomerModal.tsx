import { useState, useEffect } from "react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Field, FieldLabel, FieldError } from "@/components/ui/field"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import type { Customer, CustomerFormData } from "@/types"
import { customerSchema } from "@/lib/schemas"

interface EditCustomerModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  customer: Customer | null
  onSubmit: (data: CustomerFormData) => void
}

const customerTypes = [
  { value: "RETAIL", label: "Retail" },
  { value: "WHOLESALE", label: "Wholesale" },
  { value: "DISTRIBUTOR", label: "Distributor" },
  { value: "CORPORATE", label: "Corporate" },
]

const paymentTerms = [
  { value: "Net 7", label: "Net 7" },
  { value: "Net 15", label: "Net 15" },
  { value: "Net 30", label: "Net 30" },
  { value: "Net 45", label: "Net 45" },
  { value: "Net 60", label: "Net 60" },
]

const countries = [
  { value: "USA", label: "United States" },
  { value: "CAN", label: "Canada" },
  { value: "GBR", label: "United Kingdom" },
  { value: "AUS", label: "Australia" },
  { value: "IND", label: "India" },
]

const initialFormData: CustomerFormData = {
  customerCode: "",
  companyName: "",
  contactName: "",
  email: "",
  phone: "",
  mobile: "",
  customerType: "RETAIL",
  paymentTerms: "Net 30",
}

export default function EditCustomerModal({ open, onOpenChange, customer, onSubmit }: EditCustomerModalProps) {
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<CustomerFormData>({
    resolver: zodResolver(customerSchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  // Populate form when customer changes
  useEffect(() => {
    if (customer) {
      form.reset({
        customerCode: customer.customerCode || "",
        companyName: customer.companyName || "",
        contactName: customer.contactName || "",
        email: customer.email || "",
        phone: customer.phone || "",
        mobile: customer.mobile || "",
        billingAddress: customer.billingAddress?.address || "",
        billingCity: customer.billingAddress?.city || "",
        billingState: customer.billingAddress?.state || "",
        billingCountry: customer.billingAddress?.country || "",
        billingPostalCode: customer.billingAddress?.postalCode || "",
        shippingAddress: customer.shippingAddress?.address || "",
        shippingCity: customer.shippingAddress?.city || "",
        shippingState: customer.shippingAddress?.state || "",
        shippingCountry: customer.shippingAddress?.country || "",
        shippingPostalCode: customer.shippingAddress?.postalCode || "",
        customerType: customer.customerType || "RETAIL",
        paymentTerms: customer.paymentTerms || "Net 30",
        creditLimit: customer.creditLimit,
        taxId: customer.taxId || "",
        isActive: customer.isActive,
      })
    }
  }, [customer, form])

  const onSubmitForm = (data: CustomerFormData) => {
    setIsLoading(true)
    onSubmit(data)
    setIsLoading(false)
  }

  const handleClose = () => {
    form.reset(initialFormData)
    onOpenChange(false)
  }

  if (!customer) return null

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[600px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Edit Customer</DialogTitle>
          <DialogDescription>
            Update the customer details below.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(onSubmitForm)}>
          <div className="grid gap-6 py-4">
            {/* Customer Code */}
            <Controller
              name="customerCode"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>
                    Customer Code <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                    disabled
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Company Name */}
            <Controller
              name="companyName"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Company Name</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Company Ltd."
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />

            {/* Contact Name */}
            <Controller
              name="contactName"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>
                    Contact Name <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="John Doe"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Email and Phone */}
            <div className="grid grid-cols-2 gap-4">
              <Controller
                name="email"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor={field.name}>Email</FieldLabel>
                    <Input
                      {...field}
                      id={field.name}
                      type="email"
                      placeholder="john@example.com"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                      aria-invalid={fieldState.invalid}
                    />
                    {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                  </Field>
                )}
              />
              <Controller
                name="phone"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor={field.name}>Phone</FieldLabel>
                    <Input
                      {...field}
                      id={field.name}
                      placeholder="+1-555-0100"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                      aria-invalid={fieldState.invalid}
                    />
                  </Field>
                )}
              />
            </div>

            {/* Mobile */}
            <Controller
              name="mobile"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Mobile</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="+1-555-0101"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />

            {/* Customer Type and Payment Terms */}
            <div className="grid grid-cols-2 gap-4">
              <Controller
                name="customerType"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor={field.name}>Customer Type</FieldLabel>
                    <Select
                      value={field.value || "RETAIL"}
                      onValueChange={field.onChange}
                    >
                      <SelectTrigger id={field.name} aria-invalid={fieldState.invalid}>
                        <SelectValue placeholder="Select type" />
                      </SelectTrigger>
                      <SelectContent>
                        {customerTypes.map((type) => (
                          <SelectItem key={type.value} value={type.value}>
                            {type.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </Field>
                )}
              />
              <Controller
                name="paymentTerms"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor={field.name}>Payment Terms</FieldLabel>
                    <Select
                      value={field.value || "Net 30"}
                      onValueChange={field.onChange}
                    >
                      <SelectTrigger id={field.name} aria-invalid={fieldState.invalid}>
                        <SelectValue placeholder="Select terms" />
                      </SelectTrigger>
                      <SelectContent>
                        {paymentTerms.map((term) => (
                          <SelectItem key={term.value} value={term.value}>
                            {term.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </Field>
                )}
              />
            </div>

            {/* Credit Limit */}
            <Controller
              name="creditLimit"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Credit Limit</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    placeholder="10000.00"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseFloat(e.target.value) || undefined)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />

            {/* Billing Address */}
            <div className="space-y-3">
              <FieldLabel className="text-base font-semibold">Billing Address</FieldLabel>
              <Controller
                name="billingAddress"
                control={form.control}
                render={({ field }) => (
                  <Field>
                    <Input
                      {...field}
                      placeholder="Street Address"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                    />
                  </Field>
                )}
              />
              <div className="grid grid-cols-2 gap-2">
                <Controller
                  name="billingCity"
                  control={form.control}
                  render={({ field }) => (
                    <Input
                      {...field}
                      placeholder="City"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                    />
                  )}
                />
                <Controller
                  name="billingState"
                  control={form.control}
                  render={({ field }) => (
                    <Input
                      {...field}
                      placeholder="State"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                    />
                  )}
                />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <Controller
                  name="billingCountry"
                  control={form.control}
                  render={({ field }) => (
                    <Select
                      value={field.value || ""}
                      onValueChange={field.onChange}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Country" />
                      </SelectTrigger>
                      <SelectContent>
                        {countries.map((country) => (
                          <SelectItem key={country.value} value={country.value}>
                            {country.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  )}
                />
                <Controller
                  name="billingPostalCode"
                  control={form.control}
                  render={({ field }) => (
                    <Input
                      {...field}
                      placeholder="Postal Code"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                    />
                  )}
                />
              </div>
            </div>

            {/* Shipping Address */}
            <div className="space-y-3">
              <FieldLabel className="text-base font-semibold">Shipping Address</FieldLabel>
              <Controller
                name="shippingAddress"
                control={form.control}
                render={({ field }) => (
                  <Field>
                    <Input
                      {...field}
                      placeholder="Street Address"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                    />
                  </Field>
                )}
              />
              <div className="grid grid-cols-2 gap-2">
                <Controller
                  name="shippingCity"
                  control={form.control}
                  render={({ field }) => (
                    <Input
                      {...field}
                      placeholder="City"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                    />
                  )}
                />
                <Controller
                  name="shippingState"
                  control={form.control}
                  render={({ field }) => (
                    <Input
                      {...field}
                      placeholder="State"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                    />
                  )}
                />
              </div>
              <div className="grid grid-cols-2 gap-2">
                <Controller
                  name="shippingCountry"
                  control={form.control}
                  render={({ field }) => (
                    <Select
                      value={field.value || ""}
                      onValueChange={field.onChange}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Country" />
                      </SelectTrigger>
                      <SelectContent>
                        {countries.map((country) => (
                          <SelectItem key={country.value} value={country.value}>
                            {country.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  )}
                />
                <Controller
                  name="shippingPostalCode"
                  control={form.control}
                  render={({ field }) => (
                    <Input
                      {...field}
                      placeholder="Postal Code"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                    />
                  )}
                />
              </div>
            </div>

            {/* Tax ID */}
            <Controller
              name="taxId"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Tax ID</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="12-3456789"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancel
            </Button>
            <Button type="submit" className="bg-primary-500 hover:bg-primary-600" disabled={isLoading}>
              {isLoading ? "Saving..." : "Save Changes"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
