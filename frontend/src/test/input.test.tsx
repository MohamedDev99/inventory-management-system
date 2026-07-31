import { describe, it, expect } from "vitest"
import { render, screen } from "@testing-library/react"
import userEvent from "@testing-library/user-event"
import { Input } from "@/components/ui/input"

describe("Input", () => {
  it("should render input element", () => {
    render(<Input />)
    expect(screen.getByRole("textbox")).toBeInTheDocument()
  })

  it("should render with placeholder", () => {
    render(<Input placeholder="Enter text" />)
    expect(screen.getByPlaceholderText("Enter text")).toBeInTheDocument()
  })

  it("should render with type password", () => {
    render(<Input type="password" />)
    expect(document.querySelector("input")).toHaveAttribute("type", "password")
  })

  it("should render with custom type", () => {
    render(<Input type="email" />)
    expect(document.querySelector("input")).toHaveAttribute("type", "email")
  })

  it("should accept custom className", () => {
    const { container } = render(<Input className="custom-class" />)
    expect(container.querySelector("input")).toHaveClass("custom-class")
  })

  it("should show error styling when error prop is provided", () => {
    const { container } = render(<Input error="Error message" />)
    expect(container.querySelector("input")).toHaveClass("border-error-500")
  })

  it("should accept ref", () => {
    const ref = { current: null }
    render(<Input ref={ref} />)
    expect(ref.current).not.toBeNull()
  })

  it("should handle user input", async () => {
    render(<Input />)
    const input = screen.getByRole("textbox")
    await userEvent.type(input, "Hello")
    expect(input).toHaveValue("Hello")
  })

  it("should handle disabled prop", () => {
    render(<Input disabled />)
    expect(screen.getByRole("textbox")).toBeDisabled()
  })

  it("should handle defaultValue", () => {
    render(<Input defaultValue="default value" />)
    expect(screen.getByRole("textbox")).toHaveValue("default value")
  })

  it("should pass through other props", () => {
    render(<Input id="test-id" name="test-name" />)
    expect(screen.getByRole("textbox")).toHaveAttribute("id", "test-id")
    expect(screen.getByRole("textbox")).toHaveAttribute("name", "test-name")
  })
})