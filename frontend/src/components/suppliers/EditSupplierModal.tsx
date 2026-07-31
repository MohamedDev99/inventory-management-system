import { useState, useEffect } from "react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Field, FieldLabel, FieldError } from "@/components/ui/field"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import type { Supplier } from "@/types"
import { supplierSchema, type SupplierFormData } from "@/lib/schemas"

interface EditSupplierModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  supplier: Supplier | null
  onSubmit: (data: SupplierFormData) => void
}

const initialFormData: SupplierFormData = {
  name: "",
  code: "",
  contactPerson: "",
  email: "",
  phone: "",
  address: "",
  city: "",
  country: "US",
}

const countryCodes = [
  { value: "+1", label: "US +1" },
  { value: "+44", label: "UK +44" },
  { value: "+91", label: "IN +91" },
  { value: "+86", label: "CN +86" },
  { value: "+81", label: "JP +81" },
  { value: "+49", label: "DE +49" },
  { value: "+33", label: "FR +33" },
  { value: "+61", label: "AU +61" },
]

export default function EditSupplierModal({
  open,
  onOpenChange,
  supplier,
  onSubmit,
}: EditSupplierModalProps) {
  const [isLoading, setIsLoading] = useState(false)
  const [countryCode, setCountryCode] = useState("+1")

  const form = useForm<SupplierFormData>({
    resolver: zodResolver(supplierSchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  // Populate form when supplier changes
  useEffect(() => {
    if (supplier) {
      const phone = supplier.phone || ""
      let code = "+1"
      let phoneNumber = phone

      // Extract country code if present
      if (phone.startsWith("+1")) {
        code = "+1"
        phoneNumber = phone.slice(2)
      } else if (phone.startsWith("+44")) {
        code = "+44"
        phoneNumber = phone.slice(3)
      } else if (phone.startsWith("+91")) {
        code = "+91"
        phoneNumber = phone.slice(3)
      }

      setCountryCode(code)
      form.reset({
        name: supplier.name || "",
        code: supplier.code || "",
        contactPerson: supplier.contactPerson || "",
        email: supplier.email || "",
        phone: phoneNumber,
        address: supplier.address || "",
        city: supplier.city || "",
        country: supplier.country || "US",
      })
    }
  }, [supplier, form])

  const onSubmitForm = async (data: SupplierFormData) => {
    setIsLoading(true)
    try {
      await onSubmit({
        ...data,
        phone: countryCode + data.phone,
      })
    } catch {
      // Error handled by parent
    } finally {
      setIsLoading(false)
    }
  }

  const handleClose = () => {
    form.reset(initialFormData)
    onOpenChange(false)
  }

  if (!supplier) return null

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">Edit Supplier</DialogTitle>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(onSubmitForm)}>
          {/* Form Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {/* Full Name */}
            <Controller
              name="name"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>
                    Full Name <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Enter supplier name"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Supplier Code */}
            <Controller
              name="code"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>
                    Supplier Code <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Ex: TUW10234"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                    disabled
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Contact Person */}
            <Controller
              name="contactPerson"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Contact Person</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Enter contact person name"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />

            {/* Phone Number */}
            <div>
              <FieldLabel>Phone Number</FieldLabel>
              <div className="flex gap-2">
                <Select value={countryCode} onValueChange={setCountryCode}>
                  <SelectTrigger className="w-24">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {countryCodes.map((code) => (
                      <SelectItem key={code.value} value={code.value}>
                        {code.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <Controller
                  name="phone"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Input
                      {...field}
                      placeholder="Enter phone number"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                      className="flex-1"
                      aria-invalid={fieldState.invalid}
                    />
                  )}
                />
              </div>
            </div>

            {/* Email */}
            <Controller
              name="email"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid} className="md:col-span-2">
                  <FieldLabel htmlFor={field.name}>Email-Id</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="email"
                    placeholder="Enter email address"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* State - Not in schema but kept for UI */}
            <div>
              <FieldLabel htmlFor="state">State</FieldLabel>
              <Select>
                <SelectTrigger id="state">
                  <SelectValue placeholder="Select state" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="CA">California</SelectItem>
                  <SelectItem value="NY">New York</SelectItem>
                  <SelectItem value="TX">Texas</SelectItem>
                  <SelectItem value="FL">Florida</SelectItem>
                  <SelectItem value="WA">Washington</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* Pincode - Not in schema but kept for UI */}
            <div>
              <FieldLabel htmlFor="postalCode">Pincode</FieldLabel>
              <Input id="postalCode" placeholder="Enter pincode" />
            </div>

            {/* Address */}
            <Controller
              name="address"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid} className="md:col-span-2">
                  <FieldLabel htmlFor={field.name}>Address</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Enter full address"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />
          </div>

          {/* Footer */}
          <DialogFooter className="mt-6">
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancel
            </Button>
            <Button
              type="submit"
              className="bg-primary-500 hover:bg-primary-600"
              disabled={isLoading}
            >
              {isLoading ? "Saving..." : "Save Changes"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
