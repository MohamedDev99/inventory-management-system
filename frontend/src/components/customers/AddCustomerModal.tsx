import { useState } from "react"
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
import { customerSchema, type CustomerFormData } from "@/lib/schemas"

interface AddCustomerModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
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

export default function AddCustomerModal({ open, onOpenChange, onSubmit }: AddCustomerModalProps) {
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<CustomerFormData>({
    resolver: zodResolver(customerSchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  const onSubmitForm = (data: CustomerFormData) => {
    setIsLoading(true)
    onSubmit(data)
    form.reset(initialFormData)
    setIsLoading(false)
  }

  const handleClose = () => {
    form.reset(initialFormData)
    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[600px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Add New Customer</DialogTitle>
          <DialogDescription>
            Enter the customer details below to create a new customer.
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
                    placeholder="CUST-001"
                    aria-invalid={fieldState.invalid}
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

          {/* Bulk Upload Button */}
          <div className="mb-4">
            <Button type="button" variant="outline" className="w-full">
              <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
              </svg>
              Bulk Upload (CSV)
            </Button>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancel
            </Button>
            <Button type="submit" className="bg-primary-500 hover:bg-primary-600" disabled={isLoading}>
              {isLoading ? "Adding..." : "Add Customer"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
