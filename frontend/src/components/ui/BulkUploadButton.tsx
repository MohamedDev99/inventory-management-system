import { Upload } from "lucide-react"
import { Button } from "@/components/ui/button"

interface BulkUploadButtonProps {
  onClick?: () => void
  label?: string
  className?: string
}

export default function BulkUploadButton({
  onClick,
  label = "Bulk Upload",
  className,
}: BulkUploadButtonProps) {
  return (
    <Button
      variant="outline"
      size="sm"
      onClick={onClick}
      className={className}
    >
      <Upload className="w-4 h-4 mr-2" />
      {label}
    </Button>
  )
}
