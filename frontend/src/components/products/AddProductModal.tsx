import { useState } from "react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import toast from "react-hot-toast"
import { productSchema, type ProductFormData } from "@/lib/schemas"
import { useCategories } from "@/services/category"
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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Plus, X, Upload, FileSpreadsheet, Download } from "lucide-react"

interface AddProductModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (data: ProductFormData) => void
  isLoading?: boolean
}

interface CustomField {
  id: string
  name: string
  value: string
  fieldType: "text" | "number" | "select"
  options?: string[]
}

const initialFormData: ProductFormData = {
  sku: "",
  name: "",
  description: "",
  categoryId: undefined,
  unit: "piece",
  unitPrice: 0,
  costPrice: 0,
  reorderLevel: 0,
  minStockLevel: 0,
  barcode: "",
  imageUrl: "",
  isActive: true,
}

export default function AddProductModal({
  open,
  onOpenChange,
  onSubmit,
  isLoading = false,
}: AddProductModalProps) {
  const { data: categoriesData } = useCategories({ size: 100 })
  const categories = categoriesData?.data.content || []
  
  const [isUploading, setIsUploading] = useState(false)
  const [customFields, setCustomFields] = useState<CustomField[]>([])
  const [isCustomFieldsOpen, setIsCustomFieldsOpen] = useState(false)

  const form = useForm<ProductFormData>({
    resolver: zodResolver(productSchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return

    const validTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"]
    if (!validTypes.includes(file.type)) {
      toast.error("Please upload a valid image file (JPEG, PNG, GIF, WebP)")
      return
    }

    if (file.size > 2 * 1024 * 1024) {
      toast.error("Image size must be less than 2MB")
      return
    }

    setIsUploading(true)
    try {
      const imageUrl = URL.createObjectURL(file)
      form.setValue("imageUrl", imageUrl)
      toast.success("Image uploaded successfully")
    } catch {
      toast.error("Failed to upload image")
    } finally {
      setIsUploading(false)
    }
  }

  // Custom Fields handlers
  const addCustomField = () => {
    const newField: CustomField = {
      id: `custom-${Date.now()}`,
      name: "",
      value: "",
      fieldType: "text",
    }
    setCustomFields([...customFields, newField])
  }

  const removeCustomField = (id: string) => {
    setCustomFields(customFields.filter((field) => field.id !== id))
  }

  const updateCustomField = (id: string, updates: Partial<CustomField>) => {
    setCustomFields(
      customFields.map((field) =>
        field.id === id ? { ...field, ...updates } : field
      )
    )
  }

  const onSubmitForm = (data: ProductFormData) => {
    // Include custom fields in the submission
    const customFieldsData = customFields.reduce((acc, field) => {
      if (field.name) {
        acc[field.name] = field.value
      }
      return acc
    }, {} as Record<string, string>)
    
    onSubmit({ ...data, ...customFieldsData } as ProductFormData)
    form.reset(initialFormData)
    setCustomFields([])
  }

  const handleClose = () => {
    form.reset(initialFormData)
    setCustomFields([])
    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">Add new product</DialogTitle>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(onSubmitForm)}>
          {/* Header Actions */}
          <div className="flex justify-end gap-3 mb-4">
            <Button type="button" variant="outline" size="sm">
              <svg
                className="w-4 h-4 mr-2"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 4v16m8-8H4"
                />
              </svg>
              Add Custom Field
            </Button>
            <Button 
              type="button" 
              variant="outline" 
              size="sm"
              onClick={() => setIsCustomFieldsOpen(!isCustomFieldsOpen)}
            >
              {isCustomFieldsOpen ? "Hide Fields" : `${customFields.length} Custom Fields`}
            </Button>
            <Button type="button" variant="outline" size="sm">
              <Upload className="w-4 h-4 mr-2" />
              Bulk Upload
            </Button>
          </div>

          {/* Bulk Upload Section */}
          <BulkUploadSection />

          {/* Form Grid */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {/* Product Name */}
            <Controller
              name="name"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid} className="md:col-span-2">
                  <FieldLabel htmlFor={field.name}>
                    Product Name <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="Ex: BoomHigh"
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* SKU Code */}
            <Controller
              name="sku"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>
                    SKU Code <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="RTY1234455"
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Category */}
            <Controller
              name="categoryId"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor="categoryId">Category</FieldLabel>
                  <Select
                    value={field.value?.toString() || ""}
                    onValueChange={(value) => field.onChange(value ? parseInt(value) : undefined)}
                  >
                    <SelectTrigger id="categoryId" aria-invalid={fieldState.invalid}>
                      <SelectValue placeholder="Select category" />
                    </SelectTrigger>
                    <SelectContent>
                      {categories.map((category) => (
                        <SelectItem key={category.id} value={category.id.toString()}>
                          {category.name}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </Field>
              )}
            />

            {/* Purchasing Price */}
            <Controller
              name="costPrice"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Purchasing Price</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="Ex: $100"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseFloat(e.target.value) || 0)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Selling Price */}
            <Controller
              name="unitPrice"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>
                    Selling Price <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="Ex: $120"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseFloat(e.target.value) || 0)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            {/* Weight - Using separate field */}
            <Controller
              name="barcode"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Weight (lbs)</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="Ex: 2.5"
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />

            {/* Dimension Unit */}
            <Controller
              name="unit"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Dimension Unit</FieldLabel>
                  <Select
                    value={field.value || "piece"}
                    onValueChange={field.onChange}
                  >
                    <SelectTrigger id={field.name} aria-invalid={fieldState.invalid}>
                      <SelectValue placeholder="Select unit" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="piece">Piece</SelectItem>
                      <SelectItem value="kg">Kilogram (kg)</SelectItem>
                      <SelectItem value="lb">Pound (lb)</SelectItem>
                      <SelectItem value="g">Gram (g)</SelectItem>
                      <SelectItem value="oz">Ounce (oz)</SelectItem>
                    </SelectContent>
                  </Select>
                </Field>
              )}
            />

            {/* Dimensions - Not in schema, separate field */}
            <div>
              <FieldLabel htmlFor="dimensions">Dimensions L×B×H</FieldLabel>
              <Input id="dimensions" placeholder="20 × 30 × 40" />
            </div>

            {/* Barcode Number */}
            <Controller
              name="barcode"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Barcode Number</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="QWERTY0987"
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />

            {/* GRN Number - Not in schema, separate field */}
            <div>
              <FieldLabel htmlFor="grnNumber">GRN Number (Optional)</FieldLabel>
              <Input id="grnNumber" placeholder="QWERTY56787" />
            </div>

            {/* Recorded Stock Level */}
            <Controller
              name="reorderLevel"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Recorded Stock Level</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    min="0"
                    placeholder="Ex: 2000"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />

            {/* Warning Threshold Stock Level */}
            <Controller
              name="minStockLevel"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={field.name}>Warning Threshold Stock Level</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    min="0"
                    placeholder="Ex: 100"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />

            {/* Auto Order Stock Level - Using reorderLevel again - keeping for compatibility */}
            <Controller
              name="reorderLevel"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid}>
                  <FieldLabel htmlFor={`${field.name}-auto`}>Auto Order Stock Level</FieldLabel>
                  <Input
                    id={`${field.name}-auto`}
                    type="number"
                    min="0"
                    placeholder="Ex: 50"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
                    aria-invalid={fieldState.invalid}
                  />
                </Field>
              )}
            />
          </div>

          {/* Image Upload */}
          <div className="mt-4">
            <FieldLabel>Product Image</FieldLabel>
            <div className="border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg p-8 text-center hover:border-primary-500 transition-colors cursor-pointer relative">
              <input
                type="file"
                accept="image/*"
                onChange={handleImageUpload}
                className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
              />
              {form.watch("imageUrl") ? (
                <div className="relative inline-block">
                  <img
                    src={form.watch("imageUrl")}
                    alt="Product"
                    className="w-40 h-40 object-cover rounded-lg mx-auto"
                  />
                  <div className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 hover:opacity-100 transition-opacity rounded-lg">
                    <span className="text-white text-sm">Click to change</span>
                  </div>
                </div>
              ) : (
                <div className="py-4">
                  {isUploading ? (
                    <div className="animate-spin w-8 h-8 border-2 border-primary-500 border-t-transparent rounded-full mx-auto"></div>
                  ) : (
                    <>
                      <svg
                        className="w-12 h-12 mx-auto text-gray-400 mb-2"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={1.5}
                          d="M12 4v16m8-8H4"
                        />
                      </svg>
                      <p className="text-sm text-muted-foreground">
                        Click to upload or drag and drop
                      </p>
                      <p className="text-xs text-muted-foreground mt-1">
                        SVG, PNG, JPG or GIF (max. 400×400)
                      </p>
                    </>
                  )}
                </div>
              )}
            </div>
          </div>

          {/* Description */}
          <Controller
            name="description"
            control={form.control}
            render={({ field, fieldState }) => (
              <Field data-invalid={fieldState.invalid} className="mt-4">
                <FieldLabel htmlFor={field.name}>Description</FieldLabel>
                <Textarea
                  {...field}
                  id={field.name}
                  placeholder="Enter product description..."
                  rows={3}
                  value={field.value ?? ""}
                  aria-invalid={fieldState.invalid}
                />
              </Field>
              )}
            />

          {/* Custom Fields Section */}
          {isCustomFieldsOpen && (
            <div className="mt-6 p-4 border border-accent-200 dark:border-accent-700 rounded-lg bg-accent-50 dark:bg-accent-800/50">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-sm font-medium text-accent-900 dark:text-accent-100">
                  Custom Fields
                </h3>
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={addCustomField}
                  className="text-primary-500 border-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/30"
                >
                  <Plus className="w-4 h-4 mr-1" />
                  Add Field
                </Button>
              </div>

              {customFields.length === 0 ? (
                <p className="text-sm text-accent-500 dark:text-accent-400 text-center py-4">
                  No custom fields added. Click "Add Field" to create one.
                </p>
              ) : (
                <div className="space-y-3">
                  {customFields.map((field) => (
                    <div
                      key={field.id}
                      className="flex items-start gap-3 p-3 bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-700"
                    >
                      <div className="flex-1 grid grid-cols-1 md:grid-cols-3 gap-3">
                        <Input
                          placeholder="Field Name"
                          value={field.name}
                          onChange={(e) => updateCustomField(field.id, { name: e.target.value })}
                          className="md:col-span-1"
                        />
                        <Input
                          placeholder="Field Value"
                          value={field.value}
                          onChange={(e) => updateCustomField(field.id, { value: e.target.value })}
                          className="md:col-span-2"
                        />
                      </div>
                      <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        onClick={() => removeCustomField(field.id)}
                        className="text-error-500 hover:text-error-600 hover:bg-error-50 dark:hover:bg-error-900/30"
                      >
                        <X className="w-4 h-4" />
                      </Button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

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
              {isLoading ? "Adding..." : "Add Product"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  )
}

// Bulk Upload Section Component
function BulkUploadSection() {
  const [file, setFile] = useState<File | null>(null)
  const [isProcessing, setIsProcessing] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0]
    if (!selectedFile) return

    const validTypes = [
      "text/csv",
      "application/vnd.ms-excel",
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    ]
    
    if (!validTypes.includes(selectedFile.type)) {
      setError("Please upload a CSV or Excel file")
      return
    }

    if (selectedFile.size > 5 * 1024 * 1024) {
      setError("File size must be less than 5MB")
      return
    }

    setError(null)
    setFile(selectedFile)
  }

  const handleUpload = async () => {
    if (!file) return

    setIsProcessing(true)
    try {
      // In a real implementation, you would send the file to the server
      // For now, we'll simulate processing
      await new Promise((resolve) => setTimeout(resolve, 1000))
      toast.success(`File "${file.name}" uploaded successfully!`)
      setFile(null)
    } catch {
      toast.error("Failed to process file")
    } finally {
      setIsProcessing(false)
    }
  }

  const handleDownloadTemplate = () => {
    // Generate a sample CSV template
    const headers = ["name", "sku", "category", "unitPrice", "costPrice", "barcode", "description"]
    const sampleData = [
      ["BoomHigh", "SKU001", "Electronics", "120", "100", "123456789", "Product description"],
      ["Another Product", "SKU002", "Accessories", "50", "30", "987654321", "Another description"],
    ]
    
    const csvContent = [
      headers.join(","),
      ...sampleData.map((row) => row.map((cell) => `"${cell}"`).join(","))
    ].join("\n")

    const blob = new Blob([csvContent], { type: "text/csv" })
    const url = URL.createObjectURL(blob)
    const a = document.createElement("a")
    a.href = url
    a.download = "product_template.csv"
    a.click()
    URL.revokeObjectURL(url)
  }

  return (
    <div className="mt-4 p-4 border border-accent-200 dark:border-accent-700 rounded-lg bg-accent-50 dark:bg-accent-800/50">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-accent-900 dark:text-accent-100">
          Bulk Upload Products
        </h3>
        <Button
          variant="outline"
          size="sm"
          onClick={handleDownloadTemplate}
          className="text-accent-600 dark:text-accent-400"
        >
          <Download className="w-4 h-4 mr-1" />
          Download Template
        </Button>
      </div>

      {error && (
        <div className="mb-4 p-3 bg-error-50 dark:bg-error-900/30 text-error-700 dark:text-error-400 rounded-lg text-sm">
          {error}
        </div>
      )}

      <div className="border-2 border-dashed border-accent-300 dark:border-accent-600 rounded-lg p-6 text-center">
        <input
          type="file"
          accept=".csv,.xlsx,.xls"
          onChange={handleFileChange}
          className="hidden"
          id="bulk-upload-input"
        />
        
        {file ? (
          <div className="flex flex-col items-center">
            <FileSpreadsheet className="w-10 h-10 text-success-500 mb-2" />
            <p className="text-sm font-medium text-accent-900 dark:text-accent-100 mb-1">
              {file.name}
            </p>
            <p className="text-xs text-accent-500 dark:text-accent-400 mb-3">
              {(file.size / 1024).toFixed(1)} KB
            </p>
            <div className="flex gap-2">
              <Button
                size="sm"
                onClick={handleUpload}
                disabled={isProcessing}
                className="bg-primary-500 hover:bg-primary-600"
              >
                {isProcessing ? "Processing..." : "Upload Products"}
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setFile(null)}
              >
                Cancel
              </Button>
            </div>
          </div>
        ) : (
          <label
            htmlFor="bulk-upload-input"
            className="cursor-pointer flex flex-col items-center"
          >
            <Upload className="w-10 h-10 text-accent-400 mb-2" />
            <p className="text-sm font-medium text-accent-900 dark:text-accent-100 mb-1">
              Click to upload or drag and drop
            </p>
            <p className="text-xs text-accent-500 dark:text-accent-400">
              CSV or Excel files (max. 5MB)
            </p>
          </label>
        )}
      </div>

      <p className="text-xs text-accent-500 dark:text-accent-400 mt-3 text-center">
        Upload a CSV or Excel file with product data. The file should contain columns: name, sku, category, unitPrice, costPrice, barcode, description
      </p>
    </div>
  )
}
