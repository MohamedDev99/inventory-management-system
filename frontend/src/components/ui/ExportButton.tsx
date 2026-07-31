import { Download } from "lucide-react"
import { Button } from "@/components/ui/button"

interface ExportButtonProps {
  onClick?: () => void
  loading?: boolean
  className?: string
}

export default function ExportButton({
  onClick,
  loading = false,
  className,
}: ExportButtonProps) {
  return (
    <Button
      variant="outline"
      size="sm"
      onClick={onClick}
      disabled={loading}
      className={className}
    >
      <Download className="w-4 h-4 mr-2" />
      {loading ? "Exporting..." : "Export"}
    </Button>
  )
}
