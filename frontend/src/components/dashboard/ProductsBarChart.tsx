import { useTheme } from "@/hooks/useTheme"
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Cell } from "recharts"

const defaultProductsData = [
  { name: "Geekvape", value: 85, color: "#0ea5e9" },
  { name: "SMOK", value: 72, color: "#22c55e" },
  { name: "Vaporesso", value: 68, color: "#f97316" },
  { name: "Aspire", value: 55, color: "#8b5cf6" },
  { name: "Innokin", value: 48, color: "#ec4899" },
  { name: "Joyetech", value: 42, color: "#06b6d4" },
  { name: "Eleaf", value: 38, color: "#84cc16" },
  { name: "Lost Vape", value: 32, color: "#f43f5e" },
]

interface ProductsBarChartProps {
  collapsed?: boolean
  data?: unknown[]
  loading?: boolean
}

export default function ProductsBarChart({ collapsed = false, data, loading }: ProductsBarChartProps) {
  // Use useTheme to subscribe to theme changes
  const { isDark } = useTheme()
  
  const textColor = isDark ? "#94a3b8" : "#64748b"
  const gridColor = isDark ? "#334155" : "#e2e8f0"
  
  // Use API data if available, otherwise fall back to default
  const chartData = data && data.length > 0 ? data : defaultProductsData
  
  if (collapsed) {
    return null
  }

  return (
    <div className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 p-4">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-accent-900 dark:text-accent-100">Most Sold Products</h3>
        <select className="text-sm border border-accent-200 dark:border-accent-700 rounded-md px-2 py-1 bg-white dark:bg-accent-800 text-accent-900 dark:text-accent-100">
          <option>By Volume</option>
          <option>By Revenue</option>
        </select>
      </div>
      
      {loading ? (
        <div className="h-[300px] flex items-center justify-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500"></div>
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={300}>
          <BarChart
            data={chartData}
            layout="vertical"
            margin={{ top: 5, right: 30, left: 80, bottom: 5 }}
          >
            <CartesianGrid strokeDasharray="3 3" horizontal={true} vertical={false} stroke={gridColor} />
            <XAxis 
              type="number" 
              tick={{ fontSize: 12, fill: textColor }}
              axisLine={{ stroke: gridColor }}
              tickFormatter={(value) => `${value}%`}
            />
            <YAxis 
              type="category" 
              dataKey="name" 
              tick={{ fontSize: 12, fill: textColor }}
              axisLine={{ stroke: gridColor }}
              width={70}
            />
            <Tooltip
              contentStyle={{
                backgroundColor: isDark ? "#1e293b" : "#fff",
                border: `1px solid ${gridColor}`,
                borderRadius: "8px",
                boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
                color: isDark ? "#f8fafc" : "#1e293b",
              }}
              formatter={(value) => [`${value}%`, ""]}
              labelStyle={{ color: isDark ? "#f8fafc" : "#1e293b" }}
            />
            <Bar dataKey="value" name="Sales" radius={[0, 4, 4, 0]} barSize={24}>
              {chartData.map((entry: unknown, index: number) => {
                const entryData = entry as { color?: string }
                return <Cell key={`cell-${index}`} fill={entryData?.color || defaultProductsData[index]?.color} />
              })}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      )}
    </div>
  )
}
