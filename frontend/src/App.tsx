import { Suspense, lazy } from "react"
import { Routes, Route, Navigate } from "react-router-dom"
import { Loader2 } from "lucide-react"

import PrivateRoute from "@/components/auth/PrivateRoute"
import { AUTH_ROUTES, APP_ROUTES, ENTITY_ROUTES } from "@/constants/routes"

// Lazy load all page components for code splitting
const Landing = lazy(() => import("@/pages/Landing"))
const Login = lazy(() => import("@/pages/auth/Login"))
const Register = lazy(() => import("@/pages/auth/Register"))
// const Onboarding = lazy(() => import("@/pages/auth/Onboarding"))
const AppLayout = lazy(() => import("@/layouts/AppLayout"))
const Dashboard = lazy(() => import("@/pages/dashboard/Dashboard"))
const Products = lazy(() => import("@/pages/products/Products"))
const Suppliers = lazy(() => import("@/pages/suppliers/Suppliers"))
const Categories = lazy(() => import("@/pages/categories/Categories"))
const Warehouses = lazy(() => import("@/pages/warehouses/Warehouses"))
const Stock = lazy(() => import("@/pages/stock/Stock"))
const PurchaseOrders = lazy(() => import("@/pages/purchase-orders/PurchaseOrders"))
const SalesOrders = lazy(() => import("@/pages/sales-orders/SalesOrders"))
const Shipments = lazy(() => import("@/pages/shipments/Shipments"))
const Customers = lazy(() => import("@/pages/customers/Customers"))
const Employees = lazy(() => import("@/pages/employees/Employees"))
const Departments = lazy(() => import("@/pages/departments/Departments"))
const Payments = lazy(() => import("@/pages/payments/Payments"))
const Invoices = lazy(() => import("@/pages/invoices/Invoices"))
const Reports = lazy(() => import("@/pages/reports/Reports"))
const Notifications = lazy(() => import("@/pages/notifications/Notifications"))
const Settings = lazy(() => import("@/pages/settings/Settings"))

function PageLoader() {
  return (
    <div className="flex items-center justify-center min-h-screen">
      <Loader2 className="w-8 h-8 animate-spin text-primary-500" />
    </div>
  )
}

function App() {
  return (
    <Suspense fallback={<PageLoader />}>
      <Routes>
        {/* Public routes */}
        <Route path={AUTH_ROUTES.LANDING} element={<Landing />} />
        <Route path={AUTH_ROUTES.LOGIN} element={<Login />} />
        <Route path={AUTH_ROUTES.REGISTER} element={<Register />} />
        
        {/* Protected routes with layout */}
        <Route element={<PrivateRoute><AppLayout /></PrivateRoute>}>
          <Route path={APP_ROUTES.DASHBOARD} element={<Dashboard />} />
          <Route path={ENTITY_ROUTES.PRODUCTS} element={<Products />} />
          <Route path={ENTITY_ROUTES.SUPPLIERS} element={<Suppliers />} />
          <Route path={ENTITY_ROUTES.CATEGORIES} element={<Categories />} />
          <Route path={ENTITY_ROUTES.WAREHOUSES} element={<Warehouses />} />
          <Route path={ENTITY_ROUTES.STOCK} element={<Stock />} />
          <Route path={ENTITY_ROUTES.PURCHASE_ORDERS} element={<PurchaseOrders />} />
          <Route path={ENTITY_ROUTES.SALES_ORDERS} element={<SalesOrders />} />
          <Route path={ENTITY_ROUTES.SHIPMENTS} element={<Shipments />} />
          <Route path={ENTITY_ROUTES.CUSTOMERS} element={<Customers />} />
          <Route path={ENTITY_ROUTES.EMPLOYEES} element={<Employees />} />
          <Route path={ENTITY_ROUTES.DEPARTMENTS} element={<Departments />} />
          <Route path={ENTITY_ROUTES.PAYMENTS} element={<Payments />} />
          <Route path={ENTITY_ROUTES.INVOICES} element={<Invoices />} />
          <Route path={APP_ROUTES.REPORTS} element={<Reports />} />
          <Route path={APP_ROUTES.NOTIFICATIONS} element={<Notifications />} />
          <Route path={APP_ROUTES.SETTINGS} element={<Settings />} />
        </Route>
        
        {/* Catch all - redirect to home */}
        <Route path="*" element={<Navigate to={AUTH_ROUTES.LANDING} replace />} />
      </Routes>
    </Suspense>
  )
}

export default App
