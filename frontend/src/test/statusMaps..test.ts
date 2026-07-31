import { describe, it, expect } from "vitest"
import { StatusType, OrderStatus, PaymentStatus, ShipmentStatus, InvoiceStatus } from "@/constants/enums"
import { statusLabels, statusColors } from "@/constants/statusMaps"
describe("statusMaps", () => {
  describe("statusLabels", () => {
    it("should have labels for StatusType enum values", () => {
      expect(statusLabels[StatusType.ACTIVE]).toBeTruthy()
      expect(statusLabels[StatusType.INACTIVE]).toBeTruthy()
      expect(statusLabels[StatusType.PENDING]).toBeTruthy()
      expect(typeof statusLabels[StatusType.ACTIVE]).toBe("string")
      expect(typeof statusLabels[StatusType.INACTIVE]).toBe("string")
      expect(typeof statusLabels[StatusType.PENDING]).toBe("string")
    })

    it("should have labels for OrderStatus enum values", () => {
      expect(statusLabels[OrderStatus.DRAFT]).toBeTruthy()
      expect(statusLabels[OrderStatus.PENDING]).toBeTruthy()
      expect(statusLabels[OrderStatus.CONFIRMED]).toBeTruthy()
      expect(statusLabels[OrderStatus.PROCESSING]).toBeTruthy()
      expect(statusLabels[OrderStatus.SHIPPED]).toBeTruthy()
      expect(statusLabels[OrderStatus.DELIVERED]).toBeTruthy()
      expect(statusLabels[OrderStatus.CANCELLED]).toBeTruthy()
    })

    it("should have labels for PaymentStatus enum values", () => {
      expect(statusLabels[PaymentStatus.PENDING]).toBeTruthy()
      expect(statusLabels[PaymentStatus.PAID]).toBeTruthy()
      expect(statusLabels[PaymentStatus.FAILED]).toBeTruthy()
      expect(statusLabels[PaymentStatus.REFUNDED]).toBeTruthy()
    })

    it("should have labels for ShipmentStatus enum values", () => {
      expect(statusLabels[ShipmentStatus.PENDING]).toBeTruthy()
      expect(statusLabels[ShipmentStatus.PROCESSING]).toBeTruthy()
      expect(statusLabels[ShipmentStatus.SHIPPED]).toBeTruthy()
      expect(statusLabels[ShipmentStatus.OUT_FOR_DELIVERY]).toBeTruthy()
      expect(statusLabels[ShipmentStatus.DELIVERED]).toBeTruthy()
      expect(statusLabels[ShipmentStatus.RETURNED]).toBeTruthy()
    })

    it("should have labels for InvoiceStatus enum values", () => {
      expect(statusLabels[InvoiceStatus.DRAFT]).toBeTruthy()
      expect(statusLabels[InvoiceStatus.SENT]).toBeTruthy()
      expect(statusLabels[InvoiceStatus.PAID]).toBeTruthy()
      expect(statusLabels[InvoiceStatus.OVERDUE]).toBeTruthy()
      expect(statusLabels[InvoiceStatus.CANCELLED]).toBeTruthy()
    })

    it("should have non-empty string labels", () => {
      Object.values(statusLabels).forEach((label) => {
        expect(typeof label).toBe("string")
        expect(label.length).toBeGreaterThan(0)
      })
    })
  })

  describe("statusColors", () => {
    it("should have colors for StatusType enum values", () => {
      expect(statusColors[StatusType.ACTIVE]).toBeTruthy()
      expect(statusColors[StatusType.INACTIVE]).toBeTruthy()
      expect(statusColors[StatusType.PENDING]).toBeTruthy()
    })

    it("should have colors for OrderStatus enum values", () => {
      expect(statusColors[OrderStatus.DRAFT]).toBeTruthy()
      expect(statusColors[OrderStatus.PENDING]).toBeTruthy()
      expect(statusColors[OrderStatus.CONFIRMED]).toBeTruthy()
      expect(statusColors[OrderStatus.PROCESSING]).toBeTruthy()
      expect(statusColors[OrderStatus.SHIPPED]).toBeTruthy()
      expect(statusColors[OrderStatus.DELIVERED]).toBeTruthy()
      expect(statusColors[OrderStatus.CANCELLED]).toBeTruthy()
    })

    it("should have colors for PaymentStatus enum values", () => {
      expect(statusColors[PaymentStatus.PENDING]).toBeTruthy()
      expect(statusColors[PaymentStatus.PAID]).toBeTruthy()
      expect(statusColors[PaymentStatus.FAILED]).toBeTruthy()
      expect(statusColors[PaymentStatus.REFUNDED]).toBeTruthy()
    })

    it("should have colors for ShipmentStatus enum values", () => {
      expect(statusColors[ShipmentStatus.PENDING]).toBeTruthy()
      expect(statusColors[ShipmentStatus.PROCESSING]).toBeTruthy()
      expect(statusColors[ShipmentStatus.SHIPPED]).toBeTruthy()
      expect(statusColors[ShipmentStatus.OUT_FOR_DELIVERY]).toBeTruthy()
      expect(statusColors[ShipmentStatus.DELIVERED]).toBeTruthy()
      expect(statusColors[ShipmentStatus.RETURNED]).toBeTruthy()
    })

    it("should have colors for InvoiceStatus enum values", () => {
      expect(statusColors[InvoiceStatus.DRAFT]).toBeTruthy()
      expect(statusColors[InvoiceStatus.SENT]).toBeTruthy()
      expect(statusColors[InvoiceStatus.PAID]).toBeTruthy()
      expect(statusColors[InvoiceStatus.OVERDUE]).toBeTruthy()
      expect(statusColors[InvoiceStatus.CANCELLED]).toBeTruthy()
    })

    it("should have valid color strings (hex format)", () => {
      Object.entries(statusColors).forEach(([, color]) => {
        expect(typeof color).toBe("string")
        expect(color).toMatch(/^#[0-9A-Fa-f]{6}$|^bg-/)
      })
    })
  })

  it("should have matching keys between labels and colors", () => {
    const labelKeys = Object.keys(statusLabels)
    const colorKeys = Object.keys(statusColors)
    expect(labelKeys.sort()).toEqual(colorKeys.sort())
  })
})