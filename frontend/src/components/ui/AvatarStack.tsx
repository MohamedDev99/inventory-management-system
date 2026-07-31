import { MoreHorizontal } from "lucide-react"
import { cn } from "@/lib/utils"

interface Avatar {
  id: string | number
  name?: string
  imageUrl?: string | null
}

interface AvatarStackProps {
  avatars: Avatar[]
  max?: number
  size?: "sm" | "md" | "lg"
  className?: string
}

const sizeClasses = {
  sm: "w-6 h-6 text-xs",
  md: "w-8 h-8 text-sm",
  lg: "w-10 h-10 text-base",
}

export default function AvatarStack({
  avatars,
  max = 4,
  size = "md",
  className,
}: AvatarStackProps) {
  const displayAvatars = avatars.slice(0, max)
  const overflowCount = avatars.length - max

  const getInitials = (name: string) => {
    return name
      .split(" ")
      .map((n) => n[0])
      .join("")
      .toUpperCase()
      .slice(0, 2)
  }

  return (
    <div className={cn("flex items-center", className)}>
      <div className="flex -space-x-2">
        {displayAvatars.map((avatar, index) => (
          <div
            key={avatar.id}
            className={cn(
              "rounded-full border-2 border-white dark:border-accent-900 flex items-center justify-center font-medium",
              sizeClasses[size],
              avatar.imageUrl
                ? "bg-transparent"
                : "bg-primary-100 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400",
              index > 0 && "relative"
            )}
            style={{ zIndex: displayAvatars.length - index }}
            title={avatar.name}
          >
            {avatar.imageUrl ? (
              <img
                src={avatar.imageUrl}
                alt={avatar.name || "Avatar"}
                className="w-full h-full rounded-full object-cover"
              />
            ) : (
              getInitials(avatar.name || "U")
            )}
          </div>
        ))}
      </div>
      {overflowCount > 0 && (
        <span className="ml-2 text-sm text-accent-500 dark:text-accent-400 flex items-center">
          <MoreHorizontal className="w-3 h-3 mr-1" />
          +{overflowCount}
        </span>
      )}
    </div>
  )
}
