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
import type { Department, DepartmentFormData } from "@/types"
import { departmentSchema } from "@/lib/schemas"

interface EditDepartmentModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  department: Department | null
  onSubmit: (data: DepartmentFormData) => void
}

const initialFormData: DepartmentFormData = {
  name: "",
  code: "",
}

export default function EditDepartmentModal({ open, onOpenChange, department, onSubmit }: EditDepartmentModalProps) {
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<DepartmentFormData>({
    resolver: zodResolver(departmentSchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  useEffect(() => {
    if (department) {
      form.reset({
        name: department.name || "",
        code: department.code || "",
        warehouseId: department.warehouse?.id,
        managerId: department.manager?.id,
        email: department.email || "",
        phone: department.phone || "",
      })
    }
  }, [department, form])

  const onSubmitForm = async (data: DepartmentFormData) => {
    setIsLoading(true)
    try {
      await onSubmit(data)
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

  if (!department) return null

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Edit Department</DialogTitle>
          <DialogDescription>
            Update the department details below.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(onSubmitForm)}>
          <div className="grid gap-4 py-4">
            <div className="grid grid-cols-2 gap-4">
              <Controller
                name="name"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor={field.name}>
                      Department Name <span className="text-destructive">*</span>
                    </FieldLabel>
                    <Input
                      {...field}
                      id={field.name}
                      placeholder="Sales"
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
                      Department Code <span className="text-destructive">*</span>
                    </FieldLabel>
                    <Input
                      {...field}
                      id={field.name}
                      placeholder="DEPT-001"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                      aria-invalid={fieldState.invalid}
                      disabled
                    />
                    {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                  </Field>
                )}
              />

              <Controller
                name="warehouseId"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor="warehouseId">Warehouse</FieldLabel>
                    <Select
                      value={field.value?.toString() || ""}
                      onValueChange={(value) => field.onChange(value ? parseInt(value) : undefined)}
                    >
                      <SelectTrigger id="warehouseId" aria-invalid={fieldState.invalid}>
                        <SelectValue placeholder="Select warehouse" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="1">Main Warehouse</SelectItem>
                        <SelectItem value="2">East Warehouse</SelectItem>
                        <SelectItem value="3">West Warehouse</SelectItem>
                      </SelectContent>
                    </Select>
                  </Field>
                )}
              />

              <Controller
                name="managerId"
                control={form.control}
                render={({ field, fieldState }) => (
                  <Field data-invalid={fieldState.invalid}>
                    <FieldLabel htmlFor="managerId">Assign Manager</FieldLabel>
                    <Select
                      value={field.value?.toString() || ""}
                      onValueChange={(value) => field.onChange(value ? parseInt(value) : undefined)}
                    >
                      <SelectTrigger id="managerId" aria-invalid={fieldState.invalid}>
                        <SelectValue placeholder="Select manager" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="1">John Manager</SelectItem>
                        <SelectItem value="2">Jane Supervisor</SelectItem>
                      </SelectContent>
                    </Select>
                  </Field>
                )}
              />

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
                      placeholder="sales@moeware.com"
                      value={field.value ?? ""}
                      onChange={(e) => field.onChange(e.target.value)}
                      aria-invalid={fieldState.invalid}
                    />
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
