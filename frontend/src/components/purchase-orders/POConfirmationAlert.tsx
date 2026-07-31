import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"

interface PendingOrderData {
  supplierId: number
  warehouseId: number
  items: { itemId: number; quantityReceived: number }[]
}

interface POConfirmationAlertProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  orderData: PendingOrderData | null
  onConfirm: () => void
}

export default function POConfirmationAlert({
  open,
  onOpenChange,
  onConfirm,
}: POConfirmationAlertProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle className="text-xl font-semibold">Confirm Purchase Order</DialogTitle>
        </DialogHeader>

        <div className="py-4">
          {/* Warning Icon */}
          <div className="flex justify-center mb-4">
            <div className="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center">
              <svg
                className="w-8 h-8 text-orange-500"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                />
              </svg>
            </div>
          </div>

          {/* Warning Message */}
          <DialogDescription className="text-center text-base text-gray-700">
            Do you really want to place the purchase order!? Are you sure?
          </DialogDescription>
        </div>

        {/* Footer Buttons */}
        <DialogFooter className="gap-3">
          <Button variant="outline" onClick={() => onOpenChange(false)} className="flex-1">
            No
          </Button>
          <Button onClick={onConfirm} className="flex-1 bg-primary-500 hover:bg-primary-600">
            Yes
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}
