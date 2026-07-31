import { useState, useRef } from "react"
import { Upload, X, File } from "lucide-react"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

interface FileUploadProps {
  accept?: string
  maxSize?: number // in bytes
  value?: File | null
  onChange?: (file: File | null) => void
  label?: string
  hint?: string
  className?: string
}

const defaultAccept = ".svg,.png,.jpg,.jpeg,.pdf"
const defaultMaxSize = 2 * 1024 * 1024 // 2MB

export default function FileUpload({
  accept = defaultAccept,
  maxSize = defaultMaxSize,
  value,
  onChange,
  label = "Upload file",
  hint = "SVG, PNG, JPG or PDF (max. 2MB)",
  className,
}: FileUploadProps) {
  const [isDragging, setIsDragging] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const inputRef = useRef<HTMLInputElement>(null)

  const validateFile = (file: File): boolean => {
    setError(null)

    // Check file type
    const acceptedTypes = accept.split(",").map((t) => t.trim())
    const isValidType = acceptedTypes.some((type) => {
      if (type.startsWith(".")) {
        return file.name.toLowerCase().endsWith(type.toLowerCase())
      }
      return file.type.includes(type.replace("*", ""))
    })

    if (!isValidType) {
      setError("Invalid file type")
      return false
    }

    // Check file size
    if (file.size > maxSize) {
      setError(`File size must be less than ${maxSize / (1024 * 1024)}MB`)
      return false
    }

    return true
  }

  const handleFile = (file: File) => {
    if (validateFile(file)) {
      onChange?.(file)
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      handleFile(file)
    }
  }

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(true)
  }

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)
  }

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault()
    setIsDragging(false)

    const file = e.dataTransfer.files?.[0]
    if (file) {
      handleFile(file)
    }
  }

  const handleRemove = () => {
    onChange?.(null)
    if (inputRef.current) {
      inputRef.current.value = ""
    }
  }

  const formatFileSize = (bytes: number) => {
    if (bytes < 1024) return `${bytes} B`
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`
  }

  return (
    <div className={className}>
      <input
        ref={inputRef}
        type="file"
        accept={accept}
        onChange={handleChange}
        className="hidden"
        id="file-upload"
      />

      {value ? (
        <div className="flex items-center gap-3 p-3 bg-accent-50 dark:bg-accent-800 rounded-lg border border-accent-200 dark:border-accent-700">
          <div className="flex-1 flex items-center gap-2">
            <File className="w-5 h-5 text-accent-500" />
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-accent-900 dark:text-accent-100 truncate">
                {value.name}
              </p>
              <p className="text-xs text-accent-500">
                {formatFileSize(value.size)}
              </p>
            </div>
          </div>
          <Button
            type="button"
            variant="ghost"
            size="sm"
            onClick={handleRemove}
            className="text-accent-500 hover:text-error-500"
          >
            <X className="w-4 h-4" />
          </Button>
        </div>
      ) : (
        <label
          htmlFor="file-upload"
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          className={cn(
            "flex flex-col items-center justify-center p-6 border-2 border-dashed rounded-lg cursor-pointer transition-colors",
            isDragging
              ? "border-primary-500 bg-primary-50 dark:bg-primary-900/20"
              : "border-accent-300 dark:border-accent-600 hover:border-accent-400 dark:hover:border-accent-500"
          )}
        >
          <Upload className="w-8 h-8 text-accent-400 mb-2" />
          <p className="text-sm font-medium text-accent-900 dark:text-accent-100 mb-1">
            {label}
          </p>
          <p className="text-xs text-accent-500 dark:text-accent-400 text-center">
            {hint}
          </p>
        </label>
      )}

      {error && (
        <p className="mt-2 text-xs text-error-500">{error}</p>
      )}
    </div>
  )
}
