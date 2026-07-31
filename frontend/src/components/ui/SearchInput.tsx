import { Search, X } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

interface SearchInputProps {
  value: string
  onChange: (value: string) => void
  onSearch?: () => void
  placeholder?: string
  className?: string
}

export default function SearchInput({
  value,
  onChange,
  onSearch,
  placeholder = "Search...",
  className,
}: SearchInputProps) {
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter" && onSearch) {
      onSearch()
    }
  }

  const handleClear = () => {
    onChange("")
    onSearch?.()
  }

  return (
    <div className={cn("relative", className)}>
      <Input
        type="text"
        placeholder={placeholder}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        onKeyDown={handleKeyDown}
        className="w-64 pl-10 pr-8 bg-white dark:bg-accent-800 border-accent-200 dark:border-accent-700 text-accent-900 dark:text-accent-100"
      />
      <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-accent-500 dark:text-accent-400" />
      {value && (
        <Button
          variant="ghost"
          size="sm"
          onClick={handleClear}
          className="absolute right-1 top-1/2 -translate-y-1/2 h-6 w-6 p-0 hover:bg-transparent"
        >
          <X className="w-3 h-3 text-accent-500" />
        </Button>
      )}
    </div>
  )
}
