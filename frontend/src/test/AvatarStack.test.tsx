import { describe, it, expect } from "vitest"
import { render, screen } from "@testing-library/react"
import AvatarStack from "@/components/ui/AvatarStack"
describe("AvatarStack", () => {
  const avatars = [
    { id: "1", name: "John Doe" },
    { id: "2", name: "Jane Smith" },
    { id: "3", name: "Bob Wilson" },
  ]

  it("should render avatars", () => {
    render(<AvatarStack avatars={avatars} />)
    expect(screen.getByTitle("John Doe")).toBeInTheDocument()
    expect(screen.getByTitle("Jane Smith")).toBeInTheDocument()
    expect(screen.getByTitle("Bob Wilson")).toBeInTheDocument()
  })

  it("should display only max avatars when count exceeds max prop", () => {
    render(<AvatarStack avatars={avatars} max={2} />)
    expect(screen.getByTitle("John Doe")).toBeInTheDocument()
    expect(screen.getByTitle("Jane Smith")).toBeInTheDocument()
    expect(screen.queryByTitle("Bob Wilson")).not.toBeInTheDocument()
  })

  it("should show overflow count when avatars exceed max", () => {
    render(<AvatarStack avatars={avatars} max={2} />)
    expect(screen.getByText(/^\+1$/)).toBeInTheDocument()
  })

  it("should not show overflow when avatars equal max", () => {
    render(<AvatarStack avatars={avatars} max={3} />)
    expect(screen.queryByText(/^\+[0-9]+$/)).not.toBeInTheDocument()
  })

  it("should not show overflow when avatars fewer than max", () => {
    render(<AvatarStack avatars={[avatars[0]]} max={5} />)
    expect(screen.queryByText(/^\+[0-9]+$/)).not.toBeInTheDocument()
  })

  it("should use default max of 4", () => {
    const manyAvatars = [
      { id: "1", name: "User 1" },
      { id: "2", name: "User 2" },
      { id: "3", name: "User 3" },
      { id: "4", name: "User 4" },
      { id: "5", name: "User 5" },
    ]
    render(<AvatarStack avatars={manyAvatars} />)
    expect(screen.queryByTitle("User 5")).not.toBeInTheDocument()
    expect(screen.getByText(/^\+1$/)).toBeInTheDocument()
  })

  it("should show initials when name is provided", () => {
    render(<AvatarStack avatars={[{ id: "1", name: "John Doe" }]} />)
    expect(screen.getByText("JD")).toBeInTheDocument()
  })

  it("should show U when name is not provided", () => {
    render(<AvatarStack avatars={[{ id: "1", name: undefined }]} />)
    expect(screen.getByText("U")).toBeInTheDocument()
  })

  it("should display avatar with imageUrl", () => {
    render(<AvatarStack avatars={[{ id: "1", name: "John", imageUrl: "/avatar.jpg" }]} />)
    expect(screen.getByRole("img")).toBeInTheDocument()
  })

  it("should accept className", () => {
    const { container } = render(<AvatarStack avatars={avatars} className="custom-class" />)
    expect(container.querySelector("div")).toHaveClass("custom-class")
  })

  it("should render with sm size", () => {
    const { container } = render(<AvatarStack avatars={avatars} size="sm" />)
    expect(container.querySelector("div")).toHaveClass("w-6")
    expect(container.querySelector("div")).toHaveClass("h-6")
    expect(container.querySelector("div")).toHaveClass("text-xs")
  })

  it("should render with md size", () => {
    const { container } = render(<AvatarStack avatars={avatars} size="md" />)
    expect(container.querySelector("div")).toHaveClass("w-8")
    expect(container.querySelector("div")).toHaveClass("h-8")
    expect(container.querySelector("div")).toHaveClass("text-sm")
  })

  it("should render with lg size", () => {
    const { container } = render(<AvatarStack avatars={avatars} size="lg" />)
    expect(container.querySelector("div")).toHaveClass("w-10")
    expect(container.querySelector("div")).toHaveClass("h-10")
    expect(container.querySelector("div")).toHaveClass("text-base")
  })

  it("should handle empty avatars array", () => {
    const { container } = render(<AvatarStack avatars={[]} />)
    expect(container.querySelectorAll("div > div").length).toBe(0)
  })

  it("should handle single avatar", () => {
    render(<AvatarStack avatars={[{ id: "1", name: "Only One" }]} />)
    expect(screen.getByTitle("Only One")).toBeInTheDocument()
    expect(screen.queryByText(/^\+[0-9]+$/)).not.toBeInTheDocument()
  })

  it("should handle very long name", () => {
    render(<AvatarStack avatars={[{ id: "1", name: "Alexander Hamilton Watson Smith Jr." }]} />)
    expect(screen.getByText("AH")).toBeInTheDocument()
  })

  it("should handle single word name", () => {
    render(<AvatarStack avatars={[{ id: "1", name: "Madonna" }]} />)
    expect(screen.getByText("M")).toBeInTheDocument()
  })

  it("should convert initials to uppercase", () => {
    render(<AvatarStack avatars={[{ id: "1", name: "john doe" }]} />)
    expect(screen.getByText("JD")).toBeInTheDocument()
  })

  it("should limit initials to 2 characters", () => {
    render(<AvatarStack avatars={[{ id: "1", name: "Single" }]} />)
    expect(screen.getByText("S")).toBeInTheDocument()
    const text = screen.getByText("S").textContent
    expect(text?.length).toBeLessThanOrEqual(2)
  })

  it("should use avatar id as key", () => {
    const { container } = render(<AvatarStack avatars={avatars} />)
    const avatarDivs = container.querySelectorAll('[title]')
    expect(avatarDivs[0]).toHaveAttribute("title", "John Doe")
    expect(avatarDivs[1]).toHaveAttribute("title", "Jane Smith")
  })
})