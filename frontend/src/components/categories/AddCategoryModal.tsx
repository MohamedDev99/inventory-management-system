import { useState } from "react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Field, FieldLabel, FieldError } from "@/components/ui/field"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog"
import { categorySchema, type CategoryFormData } from "@/lib/schemas"

interface AddCategoryModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (data: CategoryFormData) => void
}

const initialFormData: CategoryFormData = {
  name: "",
  code: "",
  description: "",
}

export default function AddCategoryModal({
  open,
  onOpenChange,
  onSubmit,
}: AddCategoryModalProps) {
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<CategoryFormData>({
    resolver: zodResolver(categorySchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  const onSubmitForm = async (data: CategoryFormData) => {
    setIsLoading(true)
    try {
      await onSubmit(data)
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
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">Add New Category</DialogTitle>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(onSubmitForm)}>
          {/* Category Name */}
          <Controller
            name="name"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid} className="mb-4">
                <FieldLabel htmlFor={field.name}>
                  Category Name <span className="text-destructive">*</span>
                </FieldLabel>
                <Input
                  {...field}
                  id={field.name}
                  placeholder="Enter Category Name"
                  value={field.value ?? ""}
                  onChange={(e) => field.onChange(e.target.value)}
                  aria-invalid={fieldState.invalid}
                />
                {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
              </Field>
            )}
          />

          {/* Category ID */}
          <Controller
            name="code"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid} className="mb-4">
                <FieldLabel htmlFor={field.name}>
                  Category ID <span className="text-destructive">*</span>
                </FieldLabel>
                <Input
                  {...field}
                  id={field.name}
                  placeholder="Ex: CAR0056RTY"
                  value={field.value ?? ""}
                  onChange={(e) => field.onChange(e.target.value)}
                  aria-invalid={fieldState.invalid}
                />
                {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
              </Field>
            )}
          />

          {/* Description */}
          <Controller
            name="description"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid} className="mb-4">
                <FieldLabel htmlFor={field.name}>Description</FieldLabel>
                <Textarea
                  {...field}
                  id={field.name}
                  placeholder="Enter category description..."
                  rows={3}
                  value={field.value ?? ""}
                  onChange={(e) => field.onChange(e.target.value)}
                  aria-invalid={fieldState.invalid}
                />
              </Field>
            )}
          />

          {/* Footer */}
          <DialogFooter className="mt-4">
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancel
            </Button>
            <Button
              type="submit"
              className="bg-primary-500 hover:bg-primary-600"
              disabled={isLoading}
            >
              {isLoading ? "Adding..." : "Add New Category"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
