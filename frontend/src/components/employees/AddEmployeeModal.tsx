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
import { employeeSchema, type EmployeeFormData } from "@/lib/schemas"

interface AddEmployeeModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (data: EmployeeFormData) => void
}

const initialFormData: EmployeeFormData = {
  employeeCode: "",
  firstName: "",
  lastName: "",
  email: "",
  phone: "",
  hireDate: new Date().toISOString().split("T")[0],
  jobTitle: "",
}

export default function AddEmployeeModal({ open, onOpenChange, onSubmit }: AddEmployeeModalProps) {
  const [isLoading, setIsLoading] = useState(false)

  const form = useForm<EmployeeFormData>({
    resolver: zodResolver(employeeSchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  const onSubmitForm = async (data: EmployeeFormData) => {
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
      <DialogContent className="sm:max-w-[700px] max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Add New Employee</DialogTitle>
          <DialogDescription>
            Enter the employee details below. Fields marked with * are required.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(onSubmitForm)}>
          <div className="grid gap-6 py-4">
            {/* Two Column Layout */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {/* Left Column - Personal Info */}
              <div className="space-y-4">
                <h3 className="font-semibold text-gray-900 border-b pb-2">Personal Information</h3>
                
                <Controller
                  name="employeeCode"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>
                        Employee Code <span className="text-destructive">*</span>
                      </FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        placeholder="EMP-001"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                    </Field>
                  )}
                />

                <Controller
                  name="firstName"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>
                        First Name <span className="text-destructive">*</span>
                      </FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        placeholder="John"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                    </Field>
                  )}
                />

                <Controller
                  name="lastName"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>
                        Last Name <span className="text-destructive">*</span>
                      </FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        placeholder="Doe"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
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
                        placeholder="john@moeware.com"
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
                      <FieldLabel htmlFor={field.name}>Phone Number</FieldLabel>
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

                <Controller
                  name="jobTitle"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>Job Title</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        placeholder="Warehouse Manager"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                    </Field>
                  )}
                />

                <Controller
                  name="salary"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>Salary</FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        type="number"
                        placeholder="50000"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(parseFloat(e.target.value) || undefined)}
                        aria-invalid={fieldState.invalid}
                      />
                    </Field>
                  )}
                />
              </div>

              {/* Right Column - Work Info */}
              <div className="space-y-4">
                <h3 className="font-semibold text-gray-900 border-b pb-2">Work Information</h3>

                <Controller
                  name="hireDate"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor={field.name}>
                        Hiring Date <span className="text-destructive">*</span>
                      </FieldLabel>
                      <Input
                        {...field}
                        id={field.name}
                        type="date"
                        value={field.value ?? ""}
                        onChange={(e) => field.onChange(e.target.value)}
                        aria-invalid={fieldState.invalid}
                      />
                      {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                    </Field>
                  )}
                />

                <Controller
                  name="departmentId"
                  control={form.control}
                  render={({ field, fieldState }) => (
                    <Field data-invalid={fieldState.invalid}>
                      <FieldLabel htmlFor="departmentId">Department</FieldLabel>
                      <Select
                        value={field.value?.toString() || ""}
                        onValueChange={(value) => field.onChange(value ? parseInt(value) : undefined)}
                      >
                        <SelectTrigger id="departmentId" aria-invalid={fieldState.invalid}>
                          <SelectValue placeholder="Select department" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="1">Warehouse Operations</SelectItem>
                          <SelectItem value="2">Sales</SelectItem>
                          <SelectItem value="3">Finance</SelectItem>
                          <SelectItem value="4">Human Resources</SelectItem>
                        </SelectContent>
                      </Select>
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
                      <FieldLabel htmlFor="managerId">Manager</FieldLabel>
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

                {/* Address Section */}
                <div className="space-y-3 pt-4">
                  <h4 className="font-medium text-gray-700">Address</h4>
                  
                  <Controller
                    name="address"
                    control={form.control}
                    render={({ field }) => (
                      <Field>
                        <Input
                          {...field}
                          id="address"
                          placeholder="Street Address"
                          value={field.value ?? ""}
                          onChange={(e) => field.onChange(e.target.value)}
                        />
                      </Field>
                    )}
                  />
                  
                  <div className="grid grid-cols-2 gap-2">
                    <Controller
                      name="city"
                      control={form.control}
                      render={({ field }) => (
                        <Input
                          {...field}
                          id="city"
                          placeholder="City"
                          value={field.value ?? ""}
                          onChange={(e) => field.onChange(e.target.value)}
                        />
                      )}
                    />
                    <Controller
                      name="state"
                      control={form.control}
                      render={({ field }) => (
                        <Input
                          {...field}
                          id="state"
                          placeholder="State"
                          value={field.value ?? ""}
                          onChange={(e) => field.onChange(e.target.value)}
                        />
                      )}
                    />
                  </div>

                  <div className="grid grid-cols-2 gap-2">
                    <Controller
                      name="country"
                      control={form.control}
                      render={({ field }) => (
                        <Input
                          {...field}
                          id="country"
                          placeholder="USA"
                          value={field.value ?? ""}
                          onChange={(e) => field.onChange(e.target.value)}
                        />
                      )}
                    />
                    <Controller
                      name="postalCode"
                      control={form.control}
                      render={({ field }) => (
                        <Input
                          {...field}
                          id="postalCode"
                          placeholder="12345"
                          value={field.value ?? ""}
                          onChange={(e) => field.onChange(e.target.value)}
                        />
                      )}
                    />
                  </div>
                </div>
              </div>
            </div>

            {/* File Upload Section */}
            <div className="border-t pt-4">
              <h3 className="font-semibold text-gray-900 mb-3">Upload Documents</h3>
              <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                </svg>
                <p className="mt-2 text-sm text-gray-600">
                  Drag and drop files here, or click to browse
                </p>
                <p className="mt-1 text-xs text-gray-500">
                  SVG, PNG, JPG, PDF (max 2MB)
                </p>
                <Button type="button" variant="outline" className="mt-3">
                  Choose Files
                </Button>
              </div>
            </div>
          </div>

          <DialogFooter>
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancel
            </Button>
            <Button type="submit" className="bg-primary-500 hover:bg-primary-600" disabled={isLoading}>
              {isLoading ? "Adding..." : "Add Employee"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}
