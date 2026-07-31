import { describe, it, expect } from "vitest"
import { render, screen } from "@testing-library/react"
import { Badge, badgeVariants } from "@/components/ui/badge"

describe("Badge", () => {
  it("should render with default variant", () => {
    render(<Badge>Default</Badge>)
    expect(screen.getByText("Default")).toBeInTheDocument()
  })

  it("should render with custom variant", () => {
    const { container } = render(<Badge variant="success">Success</Badge>)
    const badge = container.querySelector("div")
    expect(badge).toHaveClass("bg-success-500")
  })

  it("should render with secondary variant", () => {
    const { container } = render(<Badge variant="secondary">Secondary</Badge>)
    const badge = container.querySelector("div")
    expect(badge).toHaveClass("bg-accent-100")
  })

  it("should render with destructive variant", () => {
    const { container } = render(<Badge variant="destructive">Destructive</Badge>)
    const badge = container.querySelector("div")
    expect(badge).toHaveClass("bg-error-500")
  })

  it("should render with outline variant", () => {
    const { container } = render(<Badge variant="outline">Outline</Badge>)
    const badge = container.querySelector("div")
    expect(badge).toHaveClass("border-accent-200")
  })

  it("should render with warning variant", () => {
    const { container } = render(<Badge variant="warning">Warning</Badge>)
    const badge = container.querySelector("div")
    expect(badge).toHaveClass("bg-warning-500")
  })

  it("should accept custom className", () => {
    const { container } = render(<Badge className="custom-class">Custom</Badge>)
    const badge = container.querySelector("div")
    expect(badge).toHaveClass("custom-class")
  })

  it("should merge custom className with variant classes", () => {
    const { container } = render(
      <Badge variant="success" className="custom-class">
        Test
      </Badge>
    )
    const badge = container.querySelector("div")
    expect(badge).toHaveClass("custom-class")
    expect(badge).toHaveClass("bg-success-500")
  })
})

describe("badgeVariants", () => {
  it("should return default variant classes", () => {
    const classes = badgeVariants({ variant: "default" })
    expect(classes).toContain("bg-primary-500")
  })

  it("should return success variant classes", () => {
    const classes = badgeVariants({ variant: "success" })
    expect(classes).toContain("bg-success-500")
  })

  it("should return warning variant classes", () => {
    const classes = badgeVariants({ variant: "warning" })
    expect(classes).toContain("bg-warning-500")
  })

  it("should return destructive variant classes", () => {
    const classes = badgeVariants({ variant: "destructive" })
    expect(classes).toContain("bg-error-500")
  })

  it("should return secondary variant classes", () => {
    const classes = badgeVariants({ variant: "secondary" })
    expect(classes).toContain("bg-accent-100")
  })

  it("should return outline variant classes", () => {
    const classes = badgeVariants({ variant: "outline" })
    expect(classes).toContain("border-accent-200")
  })
})