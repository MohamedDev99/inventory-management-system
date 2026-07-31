import { describe, it, expect } from "vitest"
import { formatCurrency, formatDate, formatDateTime, formatNumber, formatPercentage, truncate, getInitials } from "@/lib/utils"

describe("utils", () => {
  describe("formatCurrency", () => {
    it("should format amount as USD currency", () => {
      expect(formatCurrency(1000)).toBe("$1,000.00")
    })

    it("should format with custom currency", () => {
      expect(formatCurrency(1000, "EUR")).toContain("1,000.00")
    })

    it("should handle zero", () => {
      expect(formatCurrency(0)).toBe("$0.00")
    })

    it("should handle negative amounts", () => {
      expect(formatCurrency(-500)).toBe("-$500.00")
    })
  })

  describe("formatDate", () => {
    it("should format date string with default options", () => {
      const result = formatDate("2024-01-15")
      expect(result).toMatch(/Jan/)
      expect(result).toMatch(/15/)
      expect(result).toMatch(/2024/)
    })

    it("should format date object with custom options", () => {
      const date = new Date("2024-01-15")
      const result = formatDate(date, { year: "numeric", month: "2-digit", day: "2-digit" })
      expect(result).toMatch(/01/)
    })

    it("should handle ISO date string", () => {
      const result = formatDate("2024-12-25T00:00:00.000Z")
      expect(result).toMatch(/Dec/)
    })
  })

  describe("formatDateTime", () => {
    it("should include time in formatted output", () => {
      const result = formatDateTime("2024-01-15T14:30:00Z")
      expect(result).toMatch(/Jan/)
      expect(result).toMatch(/15/)
      expect(result).toMatch(/2024/)
    })
  })

  describe("formatNumber", () => {
    it("should format number with thousand separators", () => {
      expect(formatNumber(1000000)).toBe("1,000,000")
    })

    it("should handle small numbers", () => {
      expect(formatNumber(100)).toBe("100")
    })
  })

  describe("formatPercentage", () => {
    it("should format with one decimal place", () => {
      expect(formatPercentage(50.567)).toBe("50.6%")
    })

    it("should handle zero", () => {
      expect(formatPercentage(0)).toBe("0.0%")
    })

    it("should handle 100%", () => {
      expect(formatPercentage(100)).toBe("100.0%")
    })
  })

  describe("truncate", () => {
    it("should not truncate short strings", () => {
      expect(truncate("hello", 10)).toBe("hello")
    })

    it("should truncate long strings with ellipsis", () => {
      expect(truncate("hello world", 5)).toBe("hello...")
    })

    it("should handle exact length", () => {
      expect(truncate("hello", 5)).toBe("hello")
    })
  })

  describe("getInitials", () => {
    it("should get initials from two words", () => {
      expect(getInitials("John Doe")).toBe("JD")
    })

    it("should get first two initials from multiple words", () => {
      expect(getInitials("John Michael Doe")).toBe("JM")
    })

    it("should handle single word", () => {
      expect(getInitials("John")).toBe("J")
    })

    it("should handle lowercase", () => {
      expect(getInitials("john doe")).toBe("JD")
    })
  })
})