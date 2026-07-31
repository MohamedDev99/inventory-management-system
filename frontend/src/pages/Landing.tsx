import { Link } from "react-router-dom"
import { Package, TrendingUp, Warehouse, BarChart3 } from "lucide-react"
import { Button } from "@/components/ui"
import { cn } from "@/lib/utils"

const features = [
  {
    icon: TrendingUp,
    title: "Order Management",
    description: "Streamline your sales and purchase orders with automated workflows",
  },
  {
    icon: Warehouse,
    title: "Warehouse Management",
    description: "Track inventory across multiple locations in real-time",
  },
  {
    icon: Package,
    title: "Inventory Tracking",
    description: "Monitor stock levels and get alerts before items run out",
  },
  {
    icon: BarChart3,
    title: "Reports & Analytics",
    description: "Make data-driven decisions with comprehensive insights",
  },
]

export default function Landing() {
  return (
    <div className="min-h-screen flex">
      {/* Left Panel - Brand */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-primary-500 via-primary-600 to-primary-700 relative overflow-hidden">
        {/* Animated Wave Background */}
        <svg
          className="absolute inset-0 w-full h-full"
          viewBox="0 0 100 100"
          preserveAspectRatio="none"
        >
          <defs>
            <linearGradient id="waveGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="rgba(255,255,255,0.1)" />
              <stop offset="50%" stopColor="rgba(255,255,255,0.05)" />
              <stop offset="100%" stopColor="rgba(255,255,255,0)" />
            </linearGradient>
          </defs>
          <path
            d="M0 50 Q 25 30 50 50 T 100 50 V 100 H 0 Z"
            fill="url(#waveGradient)"
            className="animate-pulse"
          />
          <path
            d="M0 60 Q 25 40 50 60 T 100 60 V 100 H 0 Z"
            fill="url(#waveGradient)"
            className="animate-pulse"
            style={{ animationDelay: "0.5s" }}
          />
          <path
            d="M0 70 Q 25 50 50 70 T 100 70 V 100 H 0 Z"
            fill="url(#waveGradient)"
            className="animate-pulse"
            style={{ animationDelay: "1s" }}
          />
        </svg>

        <div className="relative z-10 flex flex-col justify-center items-center w-full p-12">
          {/* Logo */}
          <div className="flex items-center gap-3 mb-8">
            <div className="bg-white/20 backdrop-blur-sm rounded-xl p-3">
              <Package className="w-10 h-10 text-white" />
            </div>
            <span className="font-display text-4xl font-bold text-white">
              MoeWare
            </span>
          </div>

          {/* Brand Message */}
          <div className="text-center max-w-md">
            <h2 className="text-3xl font-bold text-white mb-4">
              Inventory Management
              <br />
              Made Simple
            </h2>
            <p className="text-primary-100 text-lg">
              The unified platform to manage your inventory, orders, and analytics 
              in one place.
            </p>
          </div>
        </div>

        {/* Copyright */}
        <div className="absolute bottom-6 left-0 right-0 text-center">
          <p className="text-primary-200 text-sm">
            © TheUnityWare 2024
          </p>
        </div>
      </div>

      {/* Right Panel - Hero Content */}
      <div className="w-full lg:w-1/2 bg-white dark:bg-accent-950">
        {/* Header */}
        <header className="flex justify-end items-center p-6">
          <Link to="/login">
            <Button className="bg-primary-500 hover:bg-primary-600 text-white dark:bg-primary-400 dark:hover:bg-primary-500 rounded-full px-6">
              Sign in →
            </Button>
          </Link>
        </header>

        {/* Main Content */}
        <main className="flex flex-col justify-center items-center px-6 py-12 lg:px-12">
          <div className="max-w-lg text-center lg:text-left">
            {/* Mobile Logo */}
            <div className="flex items-center gap-2 lg:hidden justify-center mb-6">
              <div className="bg-primary-500 rounded-lg p-2">
                <Package className="w-6 h-6 text-white" />
              </div>
              <span className="font-display text-2xl font-bold text-primary-600">
                MoeWare
              </span>
            </div>

            {/* Hero Headline */}
            <h1 className="text-4xl lg:text-5xl font-bold text-accent-900 dark:text-accent-100 mb-4 leading-tight">
              Technology doesn't have to feel like a different language
            </h1>

            {/* Subheadline */}
            <p className="text-lg text-accent-600 dark:text-accent-400 mb-8">
              Simplified inventory management to drive business growth and 
              strategically scale operations
            </p>

            {/* Feature Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-8">
              {features.map((feature) => (
                <div
                  key={feature.title}
                  className={cn(
                    "p-4 rounded-lg border border-accent-100 dark:border-accent-800 bg-accent-50 dark:bg-accent-900",
                    "hover:border-primary-200 dark:hover:border-primary-500 hover:bg-primary-50/50 dark:hover:bg-primary-950/30",
                    "transition-all duration-200 cursor-pointer"
                  )}
                >
                  <feature.icon className="w-6 h-6 text-primary-500 mb-2" />
                  <h3 className="font-semibold text-accent-900 dark:text-accent-100 mb-1">
                    {feature.title}
                  </h3>
                  <p className="text-sm text-accent-600 dark:text-accent-400">
                    {feature.description}
                  </p>
                </div>
              ))}
            </div>

            {/* Dashboard Preview */}
            <div className="relative rounded-xl overflow-hidden shadow-2xl border border-accent-200 dark:border-accent-700">
              <div className="bg-accent-100 dark:bg-accent-800 px-4 py-2 flex items-center gap-2">
                <div className="flex gap-1.5">
                  <div className="w-3 h-3 rounded-full bg-error-400" />
                  <div className="w-3 h-3 rounded-full bg-warning-400" />
                  <div className="w-3 h-3 rounded-full bg-success-400" />
                </div>
                <span className="text-xs text-accent-500 dark:text-accent-400 ml-2">MoeWare Dashboard</span>
              </div>
              <div className="bg-accent-50 dark:bg-accent-900 p-4 min-h-[200px] flex items-center justify-center">
                <div className="text-accent-400 dark:text-accent-600 text-sm">
                  Dashboard Preview
                </div>
              </div>
            </div>
          </div>
        </main>

        {/* Footer - Mobile Only */}
        <footer className="lg:hidden text-center py-4">
          <p className="text-sm text-accent-500 dark:text-accent-400">© TheUnityWare 2024</p>
        </footer>
      </div>
    </div>
  )
}
