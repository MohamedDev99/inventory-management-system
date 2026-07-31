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
import { createInvoiceRequestSchema, type CreateInvoiceRequest } from "@/lib/schemas/invoice/request"

interface CreateInvoiceModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (data: CreateInvoiceRequest) => void
}

const initialFormData: CreateInvoiceRequest = {
  salesOrderId: 0,
  invoiceDate: new Date().toISOString().split("T")[0],
  dueDate: undefined,
  notes: "",
}

export default function CreateInvoiceModal({
  open,
  onOpenChange,
  onSubmit,
}: CreateInvoiceModalProps) {
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<CreateInvoiceRequest>({
    resolver: zodResolver(createInvoiceRequestSchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  const onSubmitForm = async (data: CreateInvoiceRequest) => {
    setIsLoading(true)
    try {
      onSubmit(data)
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
      <DialogContent className="max-w-md max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">Create Invoice</DialogTitle>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(onSubmitForm)}>
          <div className="space-y-4">
            <Controller
              name="salesOrderId"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>
                    Sales Order ID <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    placeholder="Enter sales order ID"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            <Controller
              name="invoiceDate"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor={field.name}>Invoice Date</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="date"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                  />
                </Field>
              )}
            />

            <Controller
              name="dueDate"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor={field.name}>Due Date</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="date"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                  />
                </Field>
              )}
            />

            <Controller
              name="notes"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor={field.name}>Notes</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Enter any notes (optional)"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(e.target.value)}
                  />
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
              {isLoading ? "Creating..." : "Create Invoice"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
