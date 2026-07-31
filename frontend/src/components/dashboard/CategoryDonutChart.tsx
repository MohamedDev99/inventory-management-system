import { useTheme } from "@/hooks/useTheme"
import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from "recharts"

const defaultCategoryData = [
  { name: "C-Vapes", value: 34, color: "#0ea5e9" },
  { name: "E-Liquids", value: 24, color: "#22c55e" },
  { name: "Accessories", value: 18, color: "#f97316" },
  { name: "Coils", value: 14, color: "#8b5cf6" },
  { name: "Devices", value: 10, color: "#ec4899" },
]

interface CategoryDonutChartProps {
  collapsed?: boolean
  data?: unknown[]
  loading?: boolean
}

export default function CategoryDonutChart({ collapsed = false, data, loading }: CategoryDonutChartProps) {
  // Use useTheme to subscribe to theme changes
  const { isDark } = useTheme()
  
  const gridColor = isDark ? "#334155" : "#e2e8f0"
  
  // Use API data if available, otherwise fall back to default
  const chartData = data && data.length > 0 ? data : defaultCategoryData
  
  // Get top category for center label
  const topCategory = chartData[0] as { name?: string; value?: number } || { name: "N/A", value: 0 }
  
  if (collapsed) {
    return null
  }

  return (
    <div className="bg-white dark:bg-accent-900 rounded-lg border border-accent-200 dark:border-accent-800 p-4 relative">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-accent-900 dark:text-accent-100">Top Moving Category</h3>
        <select className="text-sm border border-accent-200 dark:border-accent-700 rounded-md px-2 py-1 bg-white dark:bg-accent-800 text-accent-900 dark:text-accent-100">
          <option>By Volume</option>
          <option>By Revenue</option>
        </select>
      </div>
      
      {loading ? (
        <div className="h-[250px] flex items-center justify-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500"></div>
        </div>
      ) : (
        <>
          <ResponsiveContainer width="100%" height={250}>
            <PieChart>
              <Pie
                data={chartData}
                cx="50%"
                cy="50%"
                innerRadius={60}
                outerRadius={90}
                paddingAngle={2}
                dataKey="value"
              >
                {chartData.map((entry: unknown, index: number) => {
                  const entryData = entry as { color?: string }
                  return <Cell key={`cell-${index}`} fill={entryData?.color || defaultCategoryData[index]?.color} />
                })}
              </Pie>
              <Tooltip
                contentStyle={{
                  backgroundColor: isDark ? "#1e293b" : "#fff",
                  border: `1px solid ${gridColor}`,
                  borderRadius: "8px",
                  boxShadow: "0 4px 6px -1px rgba(0, 0, 0, 0.1)",
                  color: isDark ? "#f8fafc" : "#1e293b",
                }}
                formatter={(value) => [`${value}%`, ""]}
              />
              <Legend
                verticalAlign="bottom"
                height={36}
                formatter={(value) => (
                  <span className="text-sm text-accent-600 dark:text-accent-400">{value}</span>
                )}
              />
            </PieChart>
          </ResponsiveContainer>
          
          {/* Center Label */}
          <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 pointer-events-none" style={{ marginTop: "-40px" }}>
            <div className="text-center">
              <div className="text-2xl font-bold text-accent-900 dark:text-accent-100">{topCategory?.name || "N/A"}</div>
              <div className="text-sm text-accent-500 dark:text-accent-400">{topCategory?.value || 0}%</div>
            </div>
          </div>
        </>
      )}
    </div>
  )
}
