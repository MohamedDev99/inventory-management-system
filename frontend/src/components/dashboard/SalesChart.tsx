import { useTheme } from "@/hooks/useTheme"
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts"

// Default mock data when no API data available
const defaultSalesData = [
  { month: "Jan", sales: 4200, transactions: 120, inventory: 3800 },
  { month: "Feb", sales: 3800, transactions: 95, inventory: 4100 },
  { month: "Mar", sales: 5100, transactions: 145, inventory: 3600 },
  { month: "Apr", sales: 4700, transactions: 130, inventory: 3900 },
  { month: "May", sales: 5600, transactions: 160, inventory: 3400 },
  { month: "Jun", sales: 6200, transactions: 175, inventory: 3200 },
  { month: "Jul", sales: 5800, transactions: 165, inventory: 3500 },
  { month: "Aug", sales: 6400, transactions: 180, inventory: 3100 },
  { month: "Sep", sales: 5900, transactions: 170, inventory: 3300 },
  { month: "Oct", sales: 7100, transactions: 195, inventory: 2900 },
  { month: "Nov", sales: 6500, transactions: 185, inventory: 3000 },
  { month: "Dec", sales: 7800, transactions: 210, inventory: 2700 },
]

interface SalesChartProps {
  collapsed?: boolean
  data?: unknown[]
  loading?: boolean
}

function SalesChartComponent({ collapsed = false, data, loading }: SalesChartProps) {
  // Use useTheme to subscribe to theme changes
  const { isDark } = useTheme()
  
  const textColor = isDark ? "#94a3b8" : "#64748b"
  const gridColor = isDark ? "#334155" : "#e2e8f0"
  
  // Use API data if available, otherwise fall back to default
  const chartData = data && data.length > 0 ? data : defaultSalesData
  
  if (collapsed) {
    return null
  }

  return (
    <div className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-accent-900 dark:text-accent-100">Monthly Sales vs Inventory Analysis</h3>
        <div className="flex items-center gap-2">
          <button className="p-1 hover:bg-accent-100 dark:hover:bg-accent-800 rounded">
            <svg className="w-4 h-4 text-accent-500 dark:text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
          </button>
          <button className="p-1 hover:bg-accent-100 dark:hover:bg-accent-800 rounded">
            <svg className="w-4 h-4 text-accent-500 dark:text-accent-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </button>
        </div>
      </div>

      {loading ? (
        <div className="h-[300px] flex items-center justify-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500"></div>
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={chartData} margin={{ top: 5, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke={gridColor} />
            <XAxis 
              dataKey="month" 
              tick={{ fontSize: 12, fill: textColor }}
              axisLine={{ stroke: gridColor }}
            />
            <YAxis 
              tick={{ fontSize: 12, fill: textColor }}
              axisLine={{ stroke: gridColor }}
              tickFormatter={(value) => `$${value / 1000}k`}
            />
            <Tooltip
              contentStyle={{
                backgroundColor: isDark ? "#1e293b" : "#fff",
                border: `1px solid ${gridColor}`,
                borderRadius: "8px",
                boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
                color: isDark ? "#f8fafc" : "#1e293b",
              }}
              formatter={(value) => [`$${Number(value).toLocaleString()}`, ""]}
              labelStyle={{ color: isDark ? "#f8fafc" : "#1e293b" }}
            />
            <Legend 
              wrapperStyle={{ paddingTop: "20px" }}
              formatter={(value) => <span className="text-sm text-accent-600 dark:text-accent-400">{value}</span>}
            />
            <Bar dataKey="sales" name="Gross Sales Revenue" fill="#0ea5e9" radius={[4, 4, 0, 0]} />
            <Bar dataKey="transactions" name="Transaction Count" fill="#fbbf24" radius={[4, 4, 0, 0]} />
            <Bar dataKey="inventory" name="Inventory Moved" fill="#ef4444" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      )}
    </div>
  )
}

export default SalesChartComponent
