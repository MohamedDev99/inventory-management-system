import { useState } from "react"
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
import { warehouseSchema, type WarehouseFormData } from "@/lib/schemas"

interface AddWarehouseModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (data: WarehouseFormData) => void
}

const initialFormData: WarehouseFormData = {
  name: "",
  code: "",
  contactPerson: "",
  email: "",
  phone: "",
  address: "",
  city: "",
  state: "",
  postalCode: "",
  country: "US",
  capacity: 0,
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

export default function AddWarehouseModal({
  open,
  onOpenChange,
  onSubmit,
}: AddWarehouseModalProps) {
  const [isLoading, setIsLoading] = useState(false)
  const [countryCode, setCountryCode] = useState("+1")

  const form = useForm<WarehouseFormData>({
    resolver: zodResolver(warehouseSchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  const onSubmitForm = async (data: WarehouseFormData) => {
    setIsLoading(true)
    try {
      onSubmit({
        ...data,
        phone: countryCode + data.phone,
      })
      form.reset(initialFormData)
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

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">Add New Warehouse</DialogTitle>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(onSubmitForm)}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Controller
              name="name"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>
                    Warehouse Name <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Enter warehouse name"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            <Controller
              name="code"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>
                    Warehouse Code <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Ex: WH001"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

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

            <div>
              <FieldLabel>Warehouse Contact Number</FieldLabel>
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

            <Controller
              name="postalCode"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Pincode</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Enter pincode"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />

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

            <Controller
              name="capacity"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid} className="md:col-span-2">
                  <FieldLabel htmlFor={field.name}>
                    Capacity (in SKUs) <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    min="0"
                    placeholder="Enter capacity in SKUs"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />
          </div>

          <DialogFooter className="mt-6">
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancel
            </Button>
            <Button
              type="submit"
              className="bg-primary-500 hover:bg-primary-600"
              disabled={isLoading}
            >
              {isLoading ? "Adding..." : "Add Warehouse"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
