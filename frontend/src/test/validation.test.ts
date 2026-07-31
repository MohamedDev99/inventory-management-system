import { describe, it, expect, vi, beforeEach } from "vitest"
import { z } from "zod"
import { validateResponse, validateAxiosResponse, withValidation } from "@/lib/utils/validation"

const mockAxiosResponse = (data: unknown):any => ({
  data,
})

describe("validation", () => {
  describe("validateResponse", () => {
    const schema = z.object({
      id: z.number(),
      name: z.string(),
    })

    it("should return validated data on success", () => {
      const data = { id: 1, name: "test" }
      const result = validateResponse(schema, data)
      expect(result).toEqual(data)
    })

    it("should throw ZodError on invalid data", () => {
      const data = { id: "invalid", name: 123 }
      expect(() => validateResponse(schema, data)).toThrow()
    })

    it("should accept custom prefix in error log", () => {
      const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {})
      const data = { id: "invalid" }
      expect(() => validateResponse(schema, data, { prefix: "Custom" })).toThrow()
      expect(consoleSpy).toHaveBeenCalledWith(
        expect.stringContaining("Custom"),
        expect.anything()
      )
      consoleSpy.mockRestore()
    })

    it("should not log errors when logErrors is false", () => {
      const consoleSpy = vi.spyOn(console, "error").mockImplementation(() => {})
      const data = { id: "invalid" }
      expect(() => validateResponse(schema, data, { logErrors: false })).toThrow()
      expect(consoleSpy).not.toHaveBeenCalled()
      consoleSpy.mockRestore()
    })

    it("should handle nested objects", () => {
      const nestedSchema = z.object({
        user: z.object({
          profile: z.object({
            name: z.string(),
          }),
        }),
      })
      const data = { user: { profile: { name: "John" } } }
      const result = validateResponse(nestedSchema, data)
      expect(result.user.profile.name).toBe("John")
    })
  })

  describe("validateAxiosResponse", () => {
    const schema = z.object({
      id: z.number(),
      name: z.string(),
    })

    it("should extract and validate data from response", () => {
      const response = mockAxiosResponse({ id: 1, name: "test" })
      const result = validateAxiosResponse(response, schema)
      expect(result).toEqual({ id: 1, name: "test" })
    })

    it("should throw on invalid data", () => {
      const response = mockAxiosResponse({ id: "invalid" })
      expect(() => validateAxiosResponse(response, schema)).toThrow()
    })
  })

  describe("withValidation", () => {
    const schema = z.object({
      id: z.number(),
      name: z.string(),
    })

    it("should call API and validate response", async () => {
      const apiCall = async () => mockAxiosResponse({ id: 1, name: "test" })
      const result = await withValidation(apiCall, schema)
      expect(result).toEqual({ id: 1, name: "test" })
    })

    it("should throw when API returns invalid data", async () => {
      const apiCall = async () => mockAxiosResponse({ id: "invalid" })
      await expect(withValidation(apiCall, schema)).rejects.toThrow()
    })
  })
})