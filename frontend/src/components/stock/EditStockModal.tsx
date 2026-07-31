import { useEffect } from "react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import type { InventoryItem } from "@/types"
import { useProducts } from "@/services/product"
import { useWarehouses } from "@/services/warehouse"
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
import { inventorySchema, type InventoryFormData } from "@/lib/schemas"

interface EditStockModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  stock: InventoryItem | null
  onSubmit: (data: InventoryFormData) => void
  isLoading?: boolean
}

const initialFormData: InventoryFormData = {
  productId: 0,
  warehouseId: 0,
  quantity: 0,
}

export default function EditStockModal({
  open,
  onOpenChange,
  stock,
  onSubmit,
  isLoading = false,
}: EditStockModalProps) {
  const { data: productsData, isLoading: productsLoading } = useProducts({ size: 100 })
  const { data: warehousesData, isLoading: warehousesLoading } = useWarehouses({ size: 100 })
  
  const products = productsData?.data.content || []
  const warehouses = warehousesData?.data.content || []

  const form = useForm<InventoryFormData>({
    resolver: zodResolver(inventorySchema),
    defaultValues: initialFormData,
    mode: "all",
  })

  useEffect(() => {
    if (stock) {
      form.reset({
        productId: stock.product?.id || 0,
        warehouseId: stock.warehouse?.id || 0,
        quantity: stock.quantity || 0,
      })
    }
  }, [stock, form])

  const isInitialLoading = productsLoading || warehousesLoading

  const onSubmitForm = (data: InventoryFormData) => {
    onSubmit(data)
  }

  const handleClose = () => {
    form.reset(initialFormData)
    onOpenChange(false)
  }

  if (!stock) return null

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">Edit Stock</DialogTitle>
        </DialogHeader>

        {initialLoading ? (
          <div className="flex items-center justify-center py-8">
            <div className="animate-spin w-8 h-8 border-2 border-primary-500 border-t-transparent rounded-full"></div>
          </div>
        ) : (
          <form onSubmit={form.handleSubmit(onSubmitForm)}>
            <Controller
              name="productId"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid} className="mb-4">
                  <FieldLabel htmlFor="productId">
                    Product <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Select
                    value={field.value?.toString() || ""}
                    onValueChange={(value) => field.onChange(parseInt(value) || 0)}
                  >
                    <SelectTrigger id="productId" aria-invalid={fieldState.invalid}>
                      <SelectValue placeholder="Select Product" />
                    </SelectTrigger>
                    <SelectContent>
                      {products.map((product) => (
                        <SelectItem key={product.id} value={product.id.toString()}>
                          {product.name} ({product.sku})
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            <Controller
              name="warehouseId"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid} className="mb-4">
                  <FieldLabel htmlFor="warehouseId">
                    Warehouse <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Select
                    value={field.value?.toString() || ""}
                    onValueChange={(value) => field.onChange(parseInt(value) || 0)}
                  >
                    <SelectTrigger id="warehouseId" aria-invalid={fieldState.invalid}>
                      <SelectValue placeholder="Select Warehouse" />
                    </SelectTrigger>
                    <SelectContent>
                      {warehouses.map((warehouse) => (
                        <SelectItem key={warehouse.id} value={warehouse.id.toString()}>
                          {warehouse.name} ({warehouse.code})
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            <Controller
              name="quantity"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid} className="mb-4">
                  <FieldLabel htmlFor={field.name}>
                    Quantity in SKUs <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Input
                    {...field}
                    id={field.name}
                    type="number"
                    min="1"
                    placeholder="Enter quantity"
                    value={field.value ?? ""}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 0)}
                    aria-invalid={fieldState.invalid}
                  />
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            <DialogFooter className="mt-4">
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
        )}
      </DialogContent>
    </Dialog>
  )
}
