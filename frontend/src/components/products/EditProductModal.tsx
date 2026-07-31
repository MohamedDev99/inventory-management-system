import { useState, useEffect } from "react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import toast from "react-hot-toast"
import { productSchema, type ProductFormData } from "@/lib/schemas"
import type { Product } from "@/types"
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
import { Plus, X } from "lucide-react"

interface EditProductModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  product: Product | null
  onSubmit: (data: ProductFormData) => void
  isLoading?: boolean
}

interface ProductVariant {
  id: string
  name: string
  sku: string
  price: number
  stock: number
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

export default function EditProductModal({
  open,
  onOpenChange,
  product,
  onSubmit,
  isLoading = false,
}: EditProductModalProps) {
  const { data: categoriesData } = useCategories({ size: 100 })
  const categories = categoriesData?.data.content || []
  
  const [isUploading, setIsUploading] = useState(false)
  const [variants, setVariants] = useState<ProductVariant[]>([])
  const [isVariantsOpen, setIsVariantsOpen] = useState(false)

  const form = useForm<ProductFormData>({
    resolver: zodResolver(productSchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  // Populate form when product changes
  useEffect(() => {
    if (product) {
      form.reset({
        sku: product.sku || "",
        name: product.name || "",
        description: product.description || "",
        categoryId: product.category?.id,
        unit: product.unit || "piece",
        unitPrice: product.unitPrice || 0,
        costPrice: product.costPrice || 0,
        reorderLevel: product.reorderLevel || 0,
        minStockLevel: product.minStockLevel || 0,
        barcode: product.barcode || "",
        imageUrl: product.imageUrl || "",
        isActive: product.isActive ?? true,
      })
    }
  }, [product, form])

  const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return

    const validTypes = ["image/jpeg", "image/png", "image/gif", "image/webp"]
    if (!validTypes.includes(file.type)) {
      toast.error("Please upload a valid image file")
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

  // Variants handlers
  const addVariant = () => {
    const newVariant: ProductVariant = {
      id: `variant-${Date.now()}`,
      name: "",
      sku: "",
      price: form.getValues("unitPrice") || 0,
      stock: 0,
    }
    setVariants([...variants, newVariant])
  }

  const removeVariant = (id: string) => {
    setVariants(variants.filter((v) => v.id !== id))
  }

  const updateVariant = (id: string, updates: Partial<ProductVariant>) => {
    setVariants(
      variants.map((v) => (v.id === id ? { ...v, ...updates } : v))
    )
  }

  const onSubmitForm = (data: ProductFormData) => {
    onSubmit(data)
  }

  const handleClose = () => {
    form.reset(initialFormData)
    onOpenChange(false)
  }

  if (!product) return null

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">Edit product</DialogTitle>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(onSubmitForm)}>
          {/* Header Actions */}
          <div className="flex justify-end gap-3 mb-4">
            <Button 
              type="button" 
              variant="outline" 
              size="sm"
              onClick={addVariant}
            >
              <Plus className="w-4 h-4 mr-2" />
              Add Variants
            </Button>
            {variants.length > 0 && (
              <Button 
                type="button" 
                variant="outline" 
                size="sm"
                onClick={() => setIsVariantsOpen(!isVariantsOpen)}
              >
                {isVariantsOpen ? "Hide Variants" : `${variants.length} Variants`}
              </Button>
            )}
          </div>

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
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor="categoryId">Category</FieldLabel>
                  <Select
                    value={field.value?.toString() || ""}
                    onValueChange={(value) => field.onChange(value ? parseInt(value) : undefined)}
                  >
                    <SelectTrigger id="categoryId">
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

            {/* Weight */}
            <Controller
              name="barcode"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor={field.name}>Weight (lbs)</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    step="0.01"
                    min="0"
                    placeholder="Ex: 2.5"
                  />
                </Field>
              )}
            />

            {/* Dimension Unit */}
            <Controller
              name="unit"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor={field.name}>Dimension Unit</FieldLabel>
                  <Select
                    value={field.value || "piece"}
                    onValueChange={field.onChange}
                  >
                    <SelectTrigger id={field.name}>
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

            {/* Dimensions */}
            <div>
              <FieldLabel htmlFor="dimensions">Dimensions L×B×H</FieldLabel>
              <Input id="dimensions" placeholder="20 × 30 × 40" />
            </div>

            {/* Barcode Number */}
            <Controller
              name="barcode"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor={field.name}>Barcode Number</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    placeholder="QWERTY0987"
                  />
                </Field>
              )}
            />

            {/* GRN Number */}
            <div>
              <FieldLabel htmlFor="grnNumber">GRN Number (Optional)</FieldLabel>
              <Input id="grnNumber" placeholder="QWERTY56787" />
            </div>

            {/* Recorded Stock Level */}
            <Controller
              name="reorderLevel"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor={field.name}>Recorded Stock Level</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    min="0"
                    placeholder="Ex: 2000"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
                  />
                </Field>
              )}
            />

            {/* Warning Threshold Stock Level */}
            <Controller
              name="minStockLevel"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor={field.name}>Warning Threshold Stock Level</FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    min="0"
                    placeholder="Ex: 100"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
                  />
                </Field>
              )}
            />

            {/* Auto Order Stock Level */}
            <Controller
              name="reorderLevel"
              control={form.control}
              render={({ field }) => (
                <Field>
                  <FieldLabel htmlFor={`${field.name}-auto`}>Auto Order Stock Level</FieldLabel>
                  <Input
                    id={`${field.name}-auto`}
                    type="number"
                    min="0"
                    placeholder="Ex: 50"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
                  />
                </Field>
              )}
            />
          </div>

          {/* Image Upload */}
          <div className="mt-4">
            <FieldLabel>Product Image</FieldLabel>
            <div className="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center hover:border-primary-500 transition-colors cursor-pointer relative">
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
                  <div className="absolute top-0 right-0 -mr-2 -mt-2 bg-primary-500 rounded-full p-1">
                    <svg
                      className="w-4 h-4 text-white"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth={2}
                        d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"
                      />
                    </svg>
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
            render={({ field }) => (
              <Field className="mt-4">
                <FieldLabel htmlFor={field.name}>Description</FieldLabel>
                <Textarea
                  {...field}
                  id={field.name}
                  placeholder="Enter product description..."
                  rows={3}
                  value={field.value ?? ""}
                />
              </Field>
            )}
          />

          {/* Variants Section */}
          {isVariantsOpen && variants.length > 0 && (
            <div className="mt-6 p-4 border border-accent-200 dark:border-accent-700 rounded-lg bg-accent-50 dark:bg-accent-800/50">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-sm font-medium text-accent-900 dark:text-accent-100">
                  Product Variants
                </h3>
                <Button
                  type="button"
                  variant="outline"
                  size="sm"
                  onClick={addVariant}
                  className="text-primary-500 border-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/30"
                >
                  <Plus className="w-4 h-4 mr-1" />
                  Add Variant
                </Button>
              </div>

              <div className="space-y-3">
                {variants.map((variant) => (
                  <div
                    key={variant.id}
                    className="flex items-start gap-3 p-3 bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-700"
                  >
                    <div className="flex-1 grid grid-cols-1 md:grid-cols-4 gap-3">
                      <Input
                        placeholder="Variant Name (e.g., Red, Large)"
                        value={variant.name}
                        onChange={(e) => updateVariant(variant.id, { name: e.target.value })}
                      />
                      <Input
                        placeholder="SKU"
                        value={variant.sku}
                        onChange={(e) => updateVariant(variant.id, { sku: e.target.value })}
                      />
                      <Input
                        type="number"
                        placeholder="Price"
                        value={variant.price}
                        onChange={(e) => updateVariant(variant.id, { price: parseFloat(e.target.value) || 0 })}
                      />
                      <Input
                        type="number"
                        placeholder="Stock"
                        value={variant.stock}
                        onChange={(e) => updateVariant(variant.id, { stock: parseInt(e.target.value) || 0 })}
                      />
                    </div>
                    <Button
                      type="button"
                      variant="ghost"
                      size="sm"
                      onClick={() => removeVariant(variant.id)}
                      className="text-error-500 hover:text-error-600 hover:bg-error-50 dark:hover:bg-error-900/30"
                    >
                      <X className="w-4 h-4" />
                    </Button>
                  </div>
                ))}
              </div>

              {variants.length === 0 && (
                <p className="text-sm text-accent-500 dark:text-accent-400 text-center py-4">
                  No variants added. Click "Add Variants" to create product variants.
                </p>
              )}
            </div>
          )}

          {/* Footer */}
          <DialogFooter className="mt-6">
            <Button type="button" variant="outline" onClick={handleClose} disabled={isLoading}>
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
