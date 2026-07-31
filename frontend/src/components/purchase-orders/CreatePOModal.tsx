import { useState, useEffect } from "react"
import { Controller, useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import toast from "react-hot-toast"
import type { Product, Supplier } from "@/types"
import api from "@/api/axios"
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
import { purchaseOrderSchema, type PurchaseOrderFormData } from "@/lib/schemas"

interface ProductLineItem {
  id: number
  productId: string
  quantity: number
  unitPrice: number
  totalPrice: number
}

interface CreatePOModalProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSubmit: (data: PurchaseOrderFormData) => void
}

const initialLineItem: ProductLineItem = {
  id: 1,
  productId: "",
  quantity: 1,
  unitPrice: 0,
  totalPrice: 0,
}

export default function CreatePOModal({
  open,
  onOpenChange,
  onSubmit,
}: CreatePOModalProps) {
  const [products, setProducts] = useState<Product[]>([])
  const [suppliers, setSuppliers] = useState<Supplier[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [initialLoading, setInitialLoading] = useState(false)
  const [lineItems, setLineItems] = useState<ProductLineItem[]>([initialLineItem])

  const form = useForm<PurchaseOrderFormData>({
    resolver: zodResolver(purchaseOrderSchema),
    defaultValues: {
      supplierId: 0,
      warehouseId: 0,
      items: [],
    },
    mode: "all",
  })

  useEffect(() => {
    if (open) {
      fetchOptions()
    }
  }, [open])

  const fetchOptions = async () => {
    setInitialLoading(true)
    try {
      const [productsRes, suppliersRes] = await Promise.all([
        api.get("/products", { params: { size: 100 } }),
        api.get("/suppliers", { params: { size: 100 } }),
      ])
      setProducts(productsRes.data.content || [])
      setSuppliers(suppliersRes.data.content || [])
    } catch {
      setProducts([
        { id: 1, name: "Product A", sku: "SKU001", unitPrice: 100 } as Product,
        { id: 2, name: "Product B", sku: "SKU002", unitPrice: 150 } as Product,
      ])
      setSuppliers([
        { id: 1, name: "Supplier A", code: "SUP001" } as Supplier,
        { id: 2, name: "Supplier B", code: "SUP002" } as Supplier,
      ])
    } finally {
      setInitialLoading(false)
    }
  }

  const addLineItem = () => {
    const newItem: ProductLineItem = {
      id: Date.now(),
      productId: "",
      quantity: 1,
      unitPrice: 0,
      totalPrice: 0,
    }
    setLineItems([...lineItems, newItem])
  }

  const removeLineItem = (id: number) => {
    if (lineItems.length > 1) {
      setLineItems(lineItems.filter((item) => item.id !== id))
    }
  }

  const updateLineItem = (id: number, field: string, value: string | number) => {
    setLineItems(
      lineItems.map((item) => {
        if (item.id === id) {
          const updated = { ...item, [field]: value }
          
          if (field === "quantity" || field === "unitPrice") {
            updated.totalPrice = updated.quantity * updated.unitPrice
          }
          
          if (field === "productId") {
            const product = products.find((p) => p.id.toString() === value)
            if (product) {
              updated.unitPrice = product.unitPrice || 0
              updated.totalPrice = updated.quantity * updated.unitPrice
            }
          }
          
          return updated
        }
        return item
      })
    )
  }

  const calculateGrandTotal = () => {
    return lineItems.reduce((sum, item) => sum + item.totalPrice, 0)
  }

  const onSubmitForm = async (data: PurchaseOrderFormData) => {
    const validItems = lineItems.filter((item) => item.productId && item.quantity > 0)
    
    if (validItems.length === 0) {
      toast.error("Please add at least one product")
      return
    }

    setIsLoading(true)
    try {
      onSubmit({
        ...data,
        items: validItems.map((item) => ({
          productId: parseInt(item.productId),
          quantityOrdered: item.quantity,
          unitPrice: item.unitPrice,
        })),
      })
      form.reset({ supplierId: 0, warehouseId: 0, items: [], expectedDeliveryDate: "" })
      setLineItems([initialLineItem])
    } catch {
      // Error handled by parent
    } finally {
      setIsLoading(false)
    }
  }

  const handleClose = () => {
    form.reset({ supplierId: 0, warehouseId: 0, items: [], expectedDeliveryDate: "" })
    setLineItems([initialLineItem])
    onOpenChange(false)
  }

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="max-w-4xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">Create New Purchase Order</DialogTitle>
        </DialogHeader>

        {initialLoading ? (
          <div className="flex items-center justify-center py-8">
            <div className="animate-spin w-8 h-8 border-2 border-primary-500 border-t-transparent rounded-full"></div>
          </div>
        ) : (
          <form onSubmit={form.handleSubmit(onSubmitForm)}>
            <div className="space-y-4">
              <div className="grid grid-cols-12 gap-4 text-sm font-medium text-muted-foreground px-2">
                <div className="col-span-4">Product</div>
                <div className="col-span-2">Quantity</div>
                <div className="col-span-2">Purchase Price/Unit</div>
                <div className="col-span-2">Total</div>
                <div className="col-span-2"></div>
              </div>

              {lineItems.map((item) => (
                <div key={item.id} className="grid grid-cols-12 gap-4 items-center">
                  <div className="col-span-4">
                    <Select
                      value={item.productId}
                      onValueChange={(value) => updateLineItem(item.id, "productId", value)}
                    >
                      <SelectTrigger>
                        <SelectValue placeholder="Ex: Vapes (TRUX1243)" />
                      </SelectTrigger>
                      <SelectContent>
                        {products.map((product) => (
                          <SelectItem key={product.id} value={product.id.toString()}>
                            {product.name} ({product.sku})
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="col-span-2">
                    <Input
                      type="number"
                      min="1"
                      value={item.quantity}
                      onChange={(e) =>
                        updateLineItem(item.id, "quantity", parseInt(e.target.value) || 0)
                      }
                    />
                  </div>

                  <div className="col-span-2">
                    <Input
                      type="number"
                      min="0"
                      step="0.01"
                      value={item.unitPrice}
                      onChange={(e) =>
                        updateLineItem(item.id, "unitPrice", parseFloat(e.target.value) || 0)
                      }
                      placeholder="$120"
                    />
                  </div>

                  <div className="col-span-2">
                    <Input
                      type="text"
                      value={`$${item.totalPrice.toFixed(2)}`}
                      readOnly
                      className="bg-gray-50"
                    />
                  </div>

                  <div className="col-span-2">
                    {lineItems.length > 1 && (
                      <button
                        type="button"
                        onClick={() => removeLineItem(item.id)}
                        className="p-2 text-red-500 hover:bg-red-50 rounded transition-colors"
                      >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M20 12H4" />
                        </svg>
                      </button>
                    )}
                  </div>
                </div>
              ))}

              <button
                type="button"
                onClick={addLineItem}
                className="flex items-center gap-2 text-primary-500 hover:text-primary-600 text-sm font-medium"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                </svg>
                Add more products
              </button>
            </div>

            <Controller
              name="supplierId"
              control={form.control}
              render={({ field, fieldState }) => (
                <Field data-invalid={fieldState.invalid} className="mt-6">
                  <FieldLabel htmlFor={field.name}>
                    Supplier <span className="text-destructive">*</span>
                  </FieldLabel>
                  <Select
                    value={field.value?.toString() || ""}
                    onValueChange={(value) => field.onChange(parseInt(value) || 0)}
                  >
                    <SelectTrigger id={field.name} aria-invalid={fieldState.invalid}>
                      <SelectValue placeholder="Ex: TUW10234" />
                    </SelectTrigger>
                    <SelectContent>
                      {suppliers.map((supplier) => (
                        <SelectItem key={supplier.id} value={supplier.id.toString()}>
                          {supplier.name} ({supplier.code})
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                  {fieldState.invalid && <FieldError errors={[fieldState.error]} />}
                </Field>
              )}
            />

            <div className="mt-4 p-4 bg-gray-50 rounded-lg">
              <div className="flex justify-between items-center">
                <span className="text-sm font-medium text-muted-foreground">Grand Total</span>
                <span className="text-xl font-bold text-gray-900">
                  ${calculateGrandTotal().toFixed(2)}
                </span>
              </div>
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
                {isLoading ? "Placing Order..." : "Place Purchase Order"}
              </Button>
            </DialogFooter>
          </form>
        )}
      </DialogContent>
    </Dialog>
  )
}
