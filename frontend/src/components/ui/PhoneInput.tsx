import { useState } from "react"
import { Input } from "@/components/ui/input"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { cn } from "@/lib/utils"

interface CountryCode {
  code: string
  name: string
  flag: string
}

const defaultCountryCodes: CountryCode[] = [
  { code: "+1", name: "United States", flag: "🇺🇸" },
  { code: "+44", name: "United Kingdom", flag: "🇬🇧" },
  { code: "+91", name: "India", flag: "🇮🇳" },
  { code: "+86", name: "China", flag: "🇨🇳" },
  { code: "+81", name: "Japan", flag: "🇯🇵" },
  { code: "+49", name: "Germany", flag: "🇩🇪" },
  { code: "+33", name: "France", flag: "🇫🇷" },
  { code: "+61", name: "Australia", flag: "🇦🇺" },
  { code: "+971", name: "UAE", flag: "🇦🇪" },
  { code: "+966", name: "Saudi Arabia", flag: "🇸🇦" },
]

interface PhoneInputProps {
  value?: string
  onChange?: (value: string) => void
  defaultCountryCode?: string
  placeholder?: string
  className?: string
}

export default function PhoneInput({
  value,
  onChange,
  defaultCountryCode = "+1",
  placeholder = "Enter phone number",
  className,
}: PhoneInputProps) {
  const [countryCode, setCountryCode] = useState(defaultCountryCode)

  const handleNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const number = e.target.value
    const fullPhone = `${countryCode}${number}`
    onChange?.(fullPhone)
  }

  return (
    <div className={cn("flex items-center gap-2", className)}>
      <Select value={countryCode} onValueChange={setCountryCode}>
        <SelectTrigger className="w-24 bg-white dark:bg-accent-800">
          <SelectValue>
            {defaultCountryCodes.find((c) => c.code === countryCode)?.flag || "🇺🇸"} {countryCode}
          </SelectValue>
        </SelectTrigger>
        <SelectContent>
          {defaultCountryCodes.map((country) => (
            <SelectItem key={country.code} value={country.code}>
              {country.flag} {country.code} {country.name}
            </SelectItem>
          ))}
        </SelectContent>
      </Select>
      
      <Input
        type="tel"
        placeholder={placeholder}
        value={value?.replace(countryCode, "") || ""}
        onChange={handleNumberChange}
        className="flex-1"
      />
    </div>
  )
}
