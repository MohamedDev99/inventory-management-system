# 📋 IMS Frontend — Detailed Development Checklist
**Project:** MoeWare Inventory Management System  
**Design System:** Tailwind CSS v4 + Shadcn/UI  
**Stack:** React, TypeScript, Recharts, React Router  
**Theme:** Blue Sky (`primary-500: #0ea5e9`) | Brand tagline: *"Technology doesn't have to feel like a different language"*

---

## 🌐 PHASE 0: Project Setup & Global Architecture

- [ ] Initialize React + TypeScript project (Vite)
- [ ] Install and configure Tailwind CSS v4 with `@theme` CSS-first config
- [ ] Install Shadcn/UI and add base components: `button`, `input`, `table`, `dialog`, `alert`, `badge`, `select`, `textarea`, `checkbox`, `card`, `toast`
- [ ] Install Recharts for dashboard analytics
- [ ] Install React Router v6 for routing
- [ ] Configure path aliases (`@/components`, `@/pages`, `@/hooks`, `@/api`, `@/types`)
- [ ] Set up global fonts: Inter (body), Poppins (display), JetBrains Mono (data)
- [ ] Set up ESLint + Prettier
- [ ] Configure Axios instance with base URL and JWT interceptor
- [ ] Create global TypeScript types (`/types/index.ts`)
- [ ] Set up React Query or SWR for data fetching/caching
- [ ] Create `/constants` folder for enums, status maps, route paths
- [ ] Set up toast notifications globally (Shadcn Toast or react-hot-toast)
- [ ] Commit: `"chore: project setup, tailwind v4, shadcn/ui, routing"`

---

## 🔐 PHASE 1: Authentication, Onboarding & Landing

### 1.1 — Landing Page (`/`)
> **Design:** Split layout — left: sky-blue brand panel with logo, right: hero content. Wave/mesh background, feature grid (4 cards), dashboard mockup preview image. Footer copyright.

- [ ] Build split-screen layout (left: sky-blue gradient panel, right: white content area)
- [ ] Add "TheUnityWare" logo with custom font styling on left panel
- [ ] Implement animated wave/mesh background graphic (SVG or CSS)
- [ ] Add hero headline: *"Technology doesn't have to feel like a different language"*
- [ ] Add subheadline: *"Simplified inventory management to drive business growth and strategically scale operations"*
- [ ] Build 4-feature grid with icons and descriptions:
  - [ ] Order Management
  - [ ] Warehouse Management
  - [ ] Inventory Tracking
  - [ ] Reports & Analytics
- [ ] Add dashboard mockup preview image (right side)
- [ ] Add "Sign in →" CTA button (top-right, sky-blue pill style)
- [ ] Add footer with copyright: `© TheUnityWare 2024`
- [ ] Make fully responsive (stack on mobile)
- [ ] Commit: `"feat: landing page with hero, features, and dashboard preview"`

### 1.2 — Login Page (`/login`)
> **Design:** Two-column layout — left: solid sky-blue panel with logo + tagline, right: white login form. "Welcome back" heading, email/password fields, remember me checkbox, forgot password link, sign in button (sky-blue primary), Google OAuth button, sign up link.

- [ ] Build two-column layout (left: sky-blue brand panel ~40% width, right: form ~60% width)
- [ ] Add logo + tagline on left panel: *"Re-imagining inventory management experience with advance data analytics for optimum performance"*
- [ ] Add copyright on bottom-left of panel
- [ ] Build login form (right panel):
  - [ ] "Welcome back" heading + subtitle
  - [ ] Email input with label
  - [ ] Password input with show/hide toggle and label
  - [ ] "Remember for 30 days" checkbox (left-aligned)
  - [ ] "Forgot password" link (right-aligned, same row as checkbox)
  - [ ] "Sign in" primary button (full-width, sky-blue primary)
  - [ ] "Sign in with Google" OAuth button (full-width, white with Google icon)
  - [ ] "Don't have an account? Sign up" link below buttons
- [ ] Implement form validation (required fields, email format)
- [ ] Handle API call to `/auth/login` with JWT token storage
- [ ] Handle Google OAuth flow
- [ ] Redirect to `/onboarding` (new users) or `/dashboard` (returning users)
- [ ] Show error toast on invalid credentials
- [ ] Commit: `"feat: login page with JWT and Google OAuth"`

### 1.3 — Onboarding Page (`/onboarding`)
> **Design:** Two-column layout — left: sky-blue panel with brand message, right: white business profile form. Fields: Business Name, Industry (search), Domain (search), Product/Services Offered (search), business type radio group, SKU size radio group. "Get Started →" button.

- [ ] Build two-column layout matching login page style
- [ ] Add left panel content: *"Engineered to handle all your inventory needs"* + description
- [ ] Build onboarding form (right panel):
  - [ ] Heading: *"Tell us a little about your business"*
  - [ ] Business Name* text input
  - [ ] Industry* searchable select/combobox (e.g., FMCG)
  - [ ] Domain* searchable select/combobox (e.g., Coffee)
  - [ ] Product/Services Offered* searchable select (e.g., PREMIUM)
  - [ ] "Which of the following best describes you?"* radio group:
    - [ ] Super-stockiest | Distributor | Retailer | Brand (4 options in a row)
  - [ ] "Storage Keeping Unit (SKU) Size"* radio group:
    - [ ] <500 | 501-1000 | 1001-5000 (row 1)
    - [ ] 5001-10000 | 10001-25000 | >25000 (row 2)
  - [ ] "Get Started →" primary button (sky-blue primary, right-aligned)
- [ ] Mark all fields with `*` as required
- [ ] Validate all required fields before submission
- [ ] Submit to `/auth/onboarding` API
- [ ] Redirect to `/dashboard` on success
- [ ] Commit: `"feat: onboarding page with business profile form"`

---

## 🏗️ PHASE 2: Layout & Navigation Shell

### 2.1 — Main App Layout
> **Design:** Fixed sidebar (left, ~240px wide), top content area with header bar, main scrollable content region.

- [ ] Create `AppLayout` component wrapping all authenticated pages
- [ ] Implement `PrivateRoute` — redirect to `/login` if no JWT
- [ ] Build fixed left sidebar (`280px` width on desktop, collapsible on mobile)
- [ ] Build sidebar navigation with icons matching design:
  - [ ] Search bar (top of sidebar)
  - [ ] Dashboard
  - [ ] Products
  - [ ] Supplier
  - [ ] Category
  - [ ] Warehouse
  - [ ] Stock
  - [ ] Purchase Order
  - [ ] Sales Order
  - [ ] Shipment
  - [ ] Customer
  - [ ] Employee
  - [ ] Department
  - [ ] Payment
  - [ ] Invoice
- [ ] Highlight active sidebar item with sky-blue background pill (`bg-primary-500`)
- [ ] Add user profile section at bottom of sidebar:
  - [ ] Avatar image
  - [ ] User name (e.g., "Olivia Rhye")
  - [ ] Role label ("Admin")
  - [ ] Logout icon button (right side)
- [ ] Add "Admin Dashboard" top bar with `</>` icon (top-right dev toggle)
- [ ] Implement responsive mobile sidebar (hamburger toggle, overlay drawer)
- [ ] Commit: `"feat: main layout with sidebar navigation and auth guard"`

---

## 📊 PHASE 3: Dashboard Page (`/dashboard`)
> **Design:** Full analytics dashboard. Top bar with time filters (1d, 7d, 1m, 3m, 6m, 1y, 3y, 5y), "Add Metrics", "Select Dates", "Filters" buttons. 6 KPI metric cards (2 rows × 3). Bar chart (Monthly Sales vs Inventory Analysis). Sankey/flow chart (Inventory Movement Landscape). Bottom row: donut chart (Top Moving Category) + horizontal bar chart (Most Sold Products).

- [ ] Build time-range filter bar: `1d | 7d | 1m | 3m | 6m | 1y | 3y | 5y` toggle buttons
- [ ] Add "Add Metrics", "Select Dates", "Filters" action buttons (top-right)
- [ ] Display "Welcome back, [Name]" heading
- [ ] Build 6 KPI metric cards (2 rows, 3 columns):
  - [ ] Average Order Volume (`$208`) with sparkline + `↑ 12% vs last month`
  - [ ] Transaction Count Orders (`100`) with sparkline + `↓ 2% vs last month`
  - [ ] Products Sold (`270`) with sparkline + `↑ 2% vs last month`
  - [ ] Gross Profit Margin (`69.10%`) with sparkline + `↑ 12% vs last month`
  - [ ] Gross Profit (`$26.4k`) with sparkline + `↓ 2% vs last month`
  - [ ] Total Net Revenue (`$1.8m`) with sparkline + `↑ 2% vs last month`
- [ ] Each KPI card has: `⋮` menu icon (top-right), metric label, value (bold, large), trend badge (green ↑ / red ↓), sparkline mini-chart
- [ ] Build "Monthly Sales Vs Inventory Analysis" stacked bar chart (Recharts):
  - [ ] X-axis: months (Jan–Dec, 2023–2024)
  - [ ] 3 data series: Gross Sales Revenue (blue), Transaction Count (yellow), Inventory Moved (red)
  - [ ] Legend at top-right
  - [ ] Collapse `^` toggle button (top-right of chart card)
  - [ ] Left/right navigation arrows on chart
- [ ] Build "Inventory Movement Landscape" Sankey/flow visualization:
  - [ ] Left side: source warehouses with percentage bars (e.g., 365 Smoke and Vape Austin 69.7%)
  - [ ] Center: "WAREHOUSE" label block
  - [ ] Right side: destination warehouses with percentage bars
  - [ ] Color-coded flow lines connecting sources to destinations
- [ ] Build "Top Moving Category" donut chart (Recharts):
  - [ ] Center label showing top category (e.g., "C-Vapes 34%")
  - [ ] "By Volume" dropdown filter (top-right)
  - [ ] Legend below chart
- [ ] Build "Most Sold Products" horizontal bar chart (Recharts):
  - [ ] Product name + percentage bar per row
  - [ ] "By Volume" dropdown filter (top-right)
- [ ] Implement `Add Metrics` modal to toggle which KPI cards are visible
- [ ] Implement date range picker for "Select Dates"
- [ ] Add real-time data refresh (polling or WebSocket placeholder)
- [ ] Make all charts responsive using `ResponsiveContainer`
- [ ] Commit: `"feat: dashboard with KPI cards, charts, and analytics"`

---

## 📦 PHASE 4: Products Page (`/products`)
> **Design:** List page with time filter bar. Table columns: Product Name (with avatar), Product ID, Supplier ID (linked, with +N badge), Category (linked), Price, Weight, Stock Level (in units), Rec. Level (in units), Actions (edit/delete icons). No stats cards on this page — just the table.

- [ ] Add page header "Products" with time filter bar (`1d 7d 1m 3m 6m 1y 3y 5y`, Select dates)
- [ ] Add toolbar: Search input, Filters button, "⊕ Add New Product" button, "Export" button (sky-blue primary, `bg-primary-500`)
- [ ] Build products data table with columns:
  - [ ] Checkbox (bulk select)
  - [ ] Product Name (with product image avatar, 40px round)
  - [ ] Product ID
  - [ ] Supplier ID (clickable link; if multiple, show first + `+N` badge)
  - [ ] Category (clickable link)
  - [ ] Price
  - [ ] Weight
  - [ ] Stock Level (in units)
  - [ ] Rec. Level (in units)
  - [ ] Actions: Edit icon (pencil, `text-primary-500`) + Delete icon (trash, red)
- [ ] Implement pagination: Previous | 1 2 3 … 8 9 10 | Next
- [ ] Implement search (filter by product name, ID, category)
- [ ] Implement column filters (category, stock status)

### 4.1 — Add New Product Modal
> **Design:** Modal with title "Add new product", "⊕ Add Custom Field" and "Bulk Upload" buttons (top-right). 3-column grid layout for fields. Image upload square (bottom-left).

- [ ] Open modal on "Add New Product" button click
- [ ] Build form with fields (3-column grid):
  - [ ] Product Name (placeholder: "Ex: BoomHigh")
  - [ ] Supplier ID (placeholder: "Ex: TUW10234")
  - [ ] Weight in lbs
  - [ ] Category (text input; placeholder: "Vapes")
  - [ ] Dimension Unit (dropdown: inch, cm, etc.)
  - [ ] Dimensions L×B×H (placeholder: "20 × 30 × 40")
  - [ ] Recorded Stock Level (placeholder: "Ex: 2000")
  - [ ] Warning Threshold Stock Level (placeholder: "Ex: 100")
  - [ ] Auto Order Stock Level (placeholder: "Ex: 50")
  - [ ] SKU Code (placeholder: "RTY1234455")
  - [ ] Barcode Number (placeholder: "QWERTY0987")
  - [ ] GRN Number (Optional) (placeholder: "QWERTY56787")
  - [ ] Insert Image upload box (400px × 400px, `+` icon, click to select)
  - [ ] Purchasing Price (placeholder: "Ex: $100")
  - [ ] Selling Price Margin (placeholder: "Ex: 20%")
  - [ ] Product Description textarea (full-width, bottom)
- [ ] Add "⊕ Add Custom Field" button to dynamically append custom fields
- [ ] Add "Bulk Upload" button (opens file picker for CSV/Excel)
- [ ] Validate required fields before submit
- [ ] Submit to POST `/products` API
- [ ] Show success toast and close modal on success
- [ ] "Add Product" primary button (full-width, sky-blue primary)

### 4.2 — Edit Product Modal
> **Design:** Same layout as Add form. Title "Edit product". Has "⊕ Add Variants" button (top-right). Image area shows existing image with edit overlay. "Save Changes" button.

- [ ] Pre-populate all fields with existing product data
- [ ] Add "⊕ Add Variants" button for product variant management
- [ ] Show existing image in image upload area with edit pencil overlay
- [ ] Submit to PUT `/products/:id` API
- [ ] "Save Changes" button (full-width, sky-blue primary)

### 4.3 — Delete Product Confirmation Modal
> **Design:** Centered alert modal. Orange warning triangle icon. Text: "Do you really want to delete the product! Are you sure?" Two buttons: "Yes" (sky-blue primary, left) and "No" (white outline, right).

- [ ] Trigger on delete icon click in table row
- [ ] Show warning triangle icon (orange)
- [ ] Display confirmation message
- [ ] "Yes" button: call DELETE `/products/:id`, show success toast, refresh table
- [ ] "No" button: close modal, no action
- [ ] Commit: `"feat: products page with add/edit/delete modals"`

---

## 🏭 PHASE 5: Supplier Page (`/supplier`)
> **Design:** Stats cards (Active: sky-blue `bg-primary-500`, Inactive: white, Deleted: white) + "Active Suppliers" table. Columns: Contact Name (with avatar), Supplier ID, Contact Name, Email-Id, Address (truncated with `…` expand), Phone Number, Actions.

- [ ] Add page header "Suppliers" with `← Back` link
- [ ] Build 3 stats cards:
  - [ ] Active Suppliers: `100` (`bg-primary-500` card, `↑ 12% vs last month`)
  - [ ] Inactive Suppliers: `19` (`↑ 12% vs last month`)
  - [ ] Deleted Suppliers: `10` (`↑ 12% vs last month`)
- [ ] Add "Active Suppliers" section heading
- [ ] Build toolbar: Search, Filters, "⊕ Add New Supplier", Export
- [ ] Build table with columns:
  - [ ] Checkbox
  - [ ] Contact Name (with avatar)
  - [ ] Supplier ID
  - [ ] Contact Name (secondary contact)
  - [ ] Email-Id
  - [ ] Address (truncated, with `…` expand tooltip showing full address)
  - [ ] Phone Number
  - [ ] Actions: Edit (pencil) + Delete (trash) + View (eye) icons
- [ ] Implement address tooltip on hover showing full address
- [ ] Implement pagination

### 5.1 — Add New Supplier Modal
> **Design:** Modal titled "Add New Supplier". 2-column layout for most fields.

- [ ] Build form fields:
  - [ ] Full Name
  - [ ] Contact Name (2-column: Full Name | Contact Name)
  - [ ] Phone Number (country code dropdown `US +1` + number input)
  - [ ] Email-Id
  - [ ] State (select) + Pincode (2-column)
  - [ ] Address (full-width)
- [ ] "Add Supplier" button (full-width, sky-blue primary)
- [ ] Validate required fields and submit to POST `/suppliers`

### 5.2 — Edit Supplier Modal
> **Design:** Same layout as Add. Title "Edit Supplier". "Save Changes" button.

- [ ] Pre-populate all fields with supplier data
- [ ] Submit to PUT `/suppliers/:id`
- [ ] "Save Changes" button (full-width, sky-blue primary)
- [ ] Commit: `"feat: supplier page with add/edit modals and stats cards"`

---

## 🗂️ PHASE 6: Category Page (`/category`)
> **Design:** Stats cards (Active: sky-blue `bg-primary-500`, Inactive: white, Deleted: white) + time filter bar + "Active Categories" table. Columns: checkbox, Category Name, Category ID, Description, Products (stacked avatars + `+N`), Actions.

- [ ] Add page header "Category" with `← Back` link and time filter bar
- [ ] Build 3 stats cards (Active: 100, Inactive: 19, Deleted: 10) with trend indicators
- [ ] Add "Active Categories" section heading
- [ ] Build toolbar: Search, Filters, "⊕ Add New Category", Export
- [ ] Build table with columns:
  - [ ] Checkbox
  - [ ] Category Name
  - [ ] Category ID
  - [ ] Description (truncated)
  - [ ] Products (stacked circular avatars, up to 4 shown + `+N` overflow badge)
  - [ ] Actions: Edit (pencil) + Delete (trash) + View (eye) icons
- [ ] Implement pagination

### 6.1 — Add New Category Modal
> **Design:** Simple modal. Fields: Category Name, Category ID, Description (textarea). "Add New Category" button.

- [ ] Build form with 3 fields:
  - [ ] Category Name (text input, placeholder: "Enter Category Name")
  - [ ] Category ID (text input, placeholder: "Ex: CAR0056RTY")
  - [ ] Description (textarea, multi-line)
- [ ] "Add New Category" button (full-width, sky-blue primary)
- [ ] Submit to POST `/categories`

### 6.2 — Edit Category Modal
> **Design:** Same layout as Add. Title "Edit Category". "Save Changes" button.

- [ ] Pre-populate Category Name, Category ID, Description
- [ ] "Save Changes" button (full-width, sky-blue primary)
- [ ] Submit to PUT `/categories/:id`
- [ ] Commit: `"feat: category page with add/edit modals and stats cards"`

---

## 🏢 PHASE 7: Warehouse Page (`/warehouse`)
> **Design:** Stats cards + time filter bar + "Active Warehouses" table. Columns: Warehouse Name, Warehouse ID (linked), Location (truncated with expand tooltip), Capacity (in SKUs), Actions.

- [ ] Add page header "Warehouses" with `← Back` link and time filter bar
- [ ] Build 3 stats cards (Active: 100, Inactive: 19, Deleted: 10)
- [ ] Add "Active Warehouses" section heading
- [ ] Build toolbar: Search, Filters, "⊕ Add New Warehouse", Export
- [ ] Build table with columns:
  - [ ] Checkbox
  - [ ] Warehouse Name
  - [ ] Warehouse ID (clickable link)
  - [ ] Location (truncated with `…`, tooltip shows full address)
  - [ ] Capacity (in SKUs)
  - [ ] Actions: Edit + Delete + View icons
- [ ] Implement pagination

### 7.1 — Add New Warehouse Modal
> **Design:** Modal with fields in 2-column layout. "Capacity (in SKUs)" field at bottom.

- [ ] Build form fields:
  - [ ] Warehouse Name + Contact Name (2-column)
  - [ ] Warehouse Contact Number (country code + number)
  - [ ] Email-Id (full-width)
  - [ ] State + Pincode (2-column)
  - [ ] Address (full-width)
  - [ ] Capacity in SKUs (full-width, placeholder: "Enter your Address" — fix to "Enter capacity")
- [ ] "Add Warehouse" button (full-width, sky-blue primary)
- [ ] Submit to POST `/warehouses`

### 7.2 — Edit Warehouse Modal
> **Design:** Same layout as Add. "Save Changes" button.

- [ ] Pre-populate all fields
- [ ] Submit to PUT `/warehouses/:id`
- [ ] "Save Changes" button
- [ ] Commit: `"feat: warehouse page with add/edit modals"`

---

## 📊 PHASE 8: Stock Page (`/stock`)
> **Design:** Time filter bar + table only (no stats cards). Table columns: Created Date, Stock ID, Product ID (linked), Warehouse ID (linked), Category (linked), Weight, Stock Level (in units), Rec. Level (in units), Actions (edit/delete/view).

- [ ] Add page header "Stock" with time filter bar
- [ ] Build toolbar: Search, Filters, "⊕ Add New Stock", Export
- [ ] Build table with columns:
  - [ ] Created Date (date + time, e.g., "Jan 4, 2022 11:30 AM")
  - [ ] Stock ID
  - [ ] Product ID (clickable link)
  - [ ] Warehouse ID (clickable link)
  - [ ] Category (clickable link)
  - [ ] Weight
  - [ ] Stock Level (in units)
  - [ ] Rec. Level (in units)
  - [ ] Actions: Edit + Delete + View icons
- [ ] Implement pagination

### 8.1 — Add New Stock Modal
> **Design:** Simple modal with 3 fields — dropdowns for Product ID and Warehouse ID, text input for Quantity.

- [ ] Build form fields:
  - [ ] Product ID (searchable dropdown, "Select Product ID")
  - [ ] Warehouse ID (searchable dropdown, "Select Product ID" — fix label)
  - [ ] Quantity in SKUs (text input, placeholder: "Enter your Address" — fix to "Enter quantity")
- [ ] "Add Stock" button (full-width, sky-blue primary)
- [ ] Submit to POST `/stock`

### 8.2 — Edit Stock Modal
> **Design:** Same 3-field layout as Add. "Save Changes" button.

- [ ] Pre-populate Product ID, Warehouse ID, Quantity
- [ ] "Save Changes" button
- [ ] Submit to PUT `/stock/:id`
- [ ] Commit: `"feat: stock page with add/edit modals"`

---

## 🛒 PHASE 9: Purchase Order Page (`/purchase-order`)
> **Design:** Time filter bar + table. Status badges: Drafted (grey), Pending (orange), Received (green), Rejected (red), In Transit (`text-primary-500` blue). Context-sensitive action icons per row based on status. Remarks tooltip on hover.

- [ ] Add page header "Purchase Order" with time filter bar
- [ ] Build toolbar: Search, Filters, "⊕ Create New Purchase Order", Export
- [ ] Build table with columns:
  - [ ] Ordered Date (date + time)
  - [ ] Purchase Order ID
  - [ ] Supplier ID (clickable link)
  - [ ] ETA (date + time, or "NA")
  - [ ] Status (badge): Drafted | Pending | Received | Rejected | In Transit
  - [ ] Actions (context-sensitive icons based on status):
    - [ ] Drafted: Edit (pencil) + Delete (trash)
    - [ ] Pending: View receipt icon + Eye/track icon
    - [ ] Received: View receipt icon + Location pin icon
    - [ ] Rejected: View receipt icon + Refresh/retry icon
    - [ ] In Transit: View receipt icon + Location pin icon
- [ ] Implement "Remarks" tooltip on row hover (e.g., "Products stock not available")
- [ ] Implement status badge colors matching design
- [ ] Implement pagination

### 9.1 — Create New Purchase Order Modal
> **Design:** Modal titled "Create New Purchase Order". Multi-row product grid (Product ID dropdown, Quantity, Purchase Price/Unit, Total Product Price per row). "⊕ Add more products" link. Supplier ID field at bottom. "Place Purchase Order" button.

- [ ] Build dynamic product line-item rows:
  - [ ] Product ID (dropdown, searchable, placeholder: "Ex: Vapes (TRUX1243)")
  - [ ] Quantity (text input)
  - [ ] Purchase Price / Unit (pre-filled, e.g., "$120")
  - [ ] Total Product Price (auto-calculated, read-only)
- [ ] Add "⊕ Add more products" link to append new rows
- [ ] Allow removal of rows (minus icon per row, except first)
- [ ] Supplier ID field at bottom (text input, placeholder: "Ex: TUW10234")
- [ ] "Place Purchase Order" button (full-width, sky-blue primary)
- [ ] Auto-calculate Total Product Price = Quantity × Price per unit
- [ ] Submit to POST `/purchase-orders`

### 9.2 — Purchase Order Confirmation Alert
> **Design:** Centered confirmation dialog. Orange triangle warning icon. "Do you really want to place the purchase order! Are you sure?" Two buttons: "Yes" (sky-blue primary) and "No" (outline).

- [ ] Show confirmation dialog when "Place Purchase Order" is clicked
- [ ] "Yes": submit to API, show success toast, close modals
- [ ] "No": dismiss dialog, return to PO form
- [ ] Commit: `"feat: purchase order page with create modal and confirmation alert"`

---

## 🧾 PHASE 10: Sales Order Page (`/sales-order`)
> **Design:** Time filter bar + table. Status badges: Pending (orange), Delivered (green), Rejected (red), In Transit (`text-primary-500` blue). Context-sensitive action icons. Remarks tooltip.

- [ ] Add page header "Sales Order" with time filter bar
- [ ] Build toolbar: Search, Filters, Export (no "Add" button — orders come from customers)
- [ ] Build table with columns:
  - [ ] Ordered Date (date + time)
  - [ ] Sales Order ID
  - [ ] Customer ID (clickable link)
  - [ ] ETD (Estimated Time of Delivery, date + time or "NA")
  - [ ] Status badge: Pending | Delivered | Rejected | In Transit
  - [ ] Actions (context-sensitive):
    - [ ] Pending (editable): Edit + Delete icons
    - [ ] Pending (submitted): Receipt + Eye/track icons
    - [ ] Delivered: Receipt + Download/invoice icons
    - [ ] Rejected: Receipt + Retry icon
    - [ ] In Transit: Receipt + Location pin icon
- [ ] Implement "Remarks" tooltip on hover (e.g., "Products stock not available")
- [ ] Implement pagination
- [ ] Commit: `"feat: sales order page with status workflow and remarks tooltip"`

---

## 🚚 PHASE 11: Shipment Page (`/shipment`)
> **Design:** Time filter bar + table. Status badges: Delayed (orange), Delivered (green), RTO (red), In Transit (`text-primary-500` blue), Re-attempt (orange-red). Context-sensitive actions. Remarks tooltip.

- [ ] Add page header "Shipment" with time filter bar
- [ ] Build toolbar: Search, Filters, Export
- [ ] Build table with columns:
  - [ ] Shipped Date (date + time)
  - [ ] Shipment ID
  - [ ] Sales Order ID (clickable link)
  - [ ] Tracking ID (clickable link)
  - [ ] ETD (date + time, or blank)
  - [ ] Status badge: Delayed | Delivered | RTO | In Transit | Re-attempt
  - [ ] Actions (context-sensitive per status):
    - [ ] Delayed: Receipt + Eye/track icons
    - [ ] Delivered: Receipt icon only
    - [ ] RTO: Receipt + Retry icon
    - [ ] In Transit: Receipt + Location pin icon
    - [ ] Re-attempt: Edit + Location pin icons
- [ ] Implement "Remarks" tooltip (e.g., "POC not available at address")
- [ ] Implement pagination
- [ ] Commit: `"feat: shipment page with status badges and tracking"`

---

## 👥 PHASE 12: Customer Page (`/customer`)
> **Design:** Stats cards (Active: sky-blue `bg-primary-500`, Inactive, Deleted) + "Active Customer" table. Columns: Contact Name (with avatar), Customer ID, Email-Id, Address (with expand tooltip), Phone Number, Actions.

- [ ] Add page header "Customers" with `← Back` link
- [ ] Build 3 stats cards (Active: 100, Inactive: 19, Deleted: 10) with trend indicators
- [ ] Add "Active Customer" section heading
- [ ] Build toolbar: Search, Filters, "⊕ Add New Customer", Export
- [ ] Build table with columns:
  - [ ] Checkbox
  - [ ] Contact Name (with avatar/initials)
  - [ ] Customer ID
  - [ ] Email-Id
  - [ ] Address (truncated with `…` expand tooltip)
  - [ ] Phone Number
  - [ ] Actions: Edit + Delete + View icons
- [ ] Implement address tooltip on hover showing full address
- [ ] Implement pagination

### 12.1 — Add New Customer Modal
> **Design:** Modal with "Bulk Upload" button (top-right). Fields: Full Name, Phone Number (with country code), Email-Id, State + Pincode (2-column), Address. "Add Customer" button.

- [ ] Add "Bulk Upload" button in modal header
- [ ] Build form fields:
  - [ ] Full Name
  - [ ] Phone Number (country code dropdown `US +1` + number)
  - [ ] Email-Id
  - [ ] State (select) + Pincode (2-column)
  - [ ] Address (full-width)
- [ ] "Add Customer" button (full-width, sky-blue primary)
- [ ] Submit to POST `/customers`

### 12.2 — Edit Customer Modal
> **Design:** Same layout as Add (without Bulk Upload). Title "Edit Customer". "Save Changes" button.

- [ ] Pre-populate all fields
- [ ] "Save Changes" button (full-width, sky-blue primary)
- [ ] Submit to PUT `/customers/:id`
- [ ] Commit: `"feat: customer page with add/edit modals and stats cards"`

---

## 👨‍💼 PHASE 13: Employee Page (`/employee`)
> **Design:** Stats cards (Active: sky-blue `bg-primary-500`, Inactive, Deleted) + "Active Employee" table. Columns: Employee Name (with avatar), Employee ID, Department ID, Warehouse ID, Hire Date (date + time), Phone Number, Actions.

- [ ] Add page header "Employee" with `← Back` link
- [ ] Build 3 stats cards (Active: 100, Inactive: 19, Deleted: 10)
- [ ] Add "Active Employee" section heading
- [ ] Build toolbar: Search, Filters, "⊕ Add New Employee", Export
- [ ] Build table with columns:
  - [ ] Checkbox
  - [ ] Employee Name (with avatar/initials)
  - [ ] Employee ID
  - [ ] Department ID
  - [ ] Warehouse ID
  - [ ] Hire Date (date + time)
  - [ ] Phone Number
  - [ ] Actions: Edit + Delete + View icons
- [ ] Implement pagination

### 13.1 — Add New Employee Modal
> **Design:** Wide modal with two-column layout. Left: personal info (Full Name, Phone, Email, State+Pincode, Address). Right: job info (Hiring Date picker, Warehouse/Store ID, Department ID, Government ID Type dropdown, File Upload area).

- [ ] Build form with two-column layout:
  - **Left column (personal info):**
    - [ ] Full Name (full-width in left col)
    - [ ] Phone Number (country code + number)
    - [ ] Email-Id
    - [ ] State + Pincode (2-column within left col)
    - [ ] Address
  - **Right column (job info):**
    - [ ] Hiring Date (date picker, default: Jan 6, 2023)
    - [ ] Warehouse/Store ID (placeholder: "Ex: BRT12234")
    - [ ] Department ID (placeholder: "Ex: BRT12234")
    - [ ] Government ID Type (dropdown: "Select Specific ID")
    - [ ] Upload Files area (ID & Offer Letter):
      - [ ] Upload icon
      - [ ] "Click to upload" link text (`text-primary-500`)
      - [ ] Accepted formats label: "SVG, PNG, JPG or PDF (max. 2MB)"
      - [ ] Drag-and-drop support
- [ ] "Add New Employee" button (full-width, sky-blue primary)
- [ ] Submit to POST `/employees`
- [ ] Handle file upload (multipart/form-data)

### 13.2 — Edit Employee Modal
> **Design:** Same two-column layout as Add. Title "Edit Employee". "Save Changes" button.

- [ ] Pre-populate all fields including existing uploaded files (show file name)
- [ ] Allow replacing uploaded file
- [ ] "Save Changes" button (full-width, sky-blue primary)
- [ ] Submit to PUT `/employees/:id`
- [ ] Commit: `"feat: employee page with add/edit modals including file upload"`

---

## 🏛️ PHASE 14: Department Page (`/department`)
> **Design:** Stats cards (Active: sky-blue `bg-primary-500`, Inactive, Deleted) + "Active Departments" table. Columns: Department Name, Department ID, Warehouse ID, Manager ID, Email-Id (truncated), Phone Number, Actions.

- [ ] Add page header "Department" with `← Back` link
- [ ] Build 3 stats cards (Active: 100, Inactive: 19, Deleted: 10)
- [ ] Add "Active Departments" section heading
- [ ] Build toolbar: Search, Filters, "⊕ Add New Department", Export
- [ ] Build table with columns:
  - [ ] Checkbox
  - [ ] Department Name
  - [ ] Department ID
  - [ ] Warehouse ID
  - [ ] Manager ID
  - [ ] Email-Id (truncated with `…`)
  - [ ] Phone Number
  - [ ] Actions: Edit + Delete + View icons
- [ ] Implement pagination

### 14.1 — Add New Department Modal
> **Design:** Wide modal with 2×2 field grid. Fields: Department Name, Date of Creation (date picker), Assign Manager (dropdown), Warehouse ID (dropdown). "Add New Department" button.

- [ ] Add "Bulk Upload" button in modal header
- [ ] Build form fields (2-column grid):
  - [ ] Department Name (text input, placeholder: "Enter Full Name")
  - [ ] Date of Creation (date picker, default: Jan 6, 2023)
  - [ ] Assign Manager (searchable dropdown, "Choose Manager Here")
  - [ ] Warehouse ID (searchable dropdown, "Select Specific ID")
- [ ] "Add New Department" button (full-width, sky-blue primary)
- [ ] Submit to POST `/departments`

### 14.2 — Edit Department Modal
> **Design:** Same layout as Add. "Save Changes" button.

- [ ] Pre-populate all fields
- [ ] "Save Changes" button (full-width, sky-blue primary)
- [ ] Submit to PUT `/departments/:id`
- [ ] Commit: `"feat: department page with add/edit modals"`

---

## 💳 PHASE 15: Payment Page (`/payment`)
> **Design:** Stats cards (Active: sky-blue `bg-primary-500`, Inactive, Deleted) + "Active Payment" table. Note: design shows employee-style columns — should be adapted for payment context (Employee Name, Employee ID, Department ID, Hire Date, Email-Id, Phone Number).

- [ ] Add page header "Payment" with `← Back` link
- [ ] Build 3 stats cards (Active: 100, Inactive: 19, Deleted: 10)
- [ ] Add "Active Payment" section heading
- [ ] Build toolbar: Search, Filters, "⊕ Add New Employee" (update label to payment), Export
- [ ] Build table with columns:
  - [ ] Checkbox
  - [ ] Employee Name (with avatar)
  - [ ] Employee ID
  - [ ] Department ID
  - [ ] Hire Date (date + time)
  - [ ] Email-Id (truncated with `…`)
  - [ ] Phone Number
  - [ ] Actions: Edit + Delete + View icons
- [ ] Implement pagination

### 15.1 — Add New Payment Modal
> **Design:** Wide two-column modal. Left: personal fields (Full Name, Phone, Email, State+Pincode, Address). Right: payment-specific fields (Hiring Date, Government ID Type, Upload Files).

- [ ] Build form with two-column layout:
  - **Left column:**
    - [ ] Full Name
    - [ ] Phone Number (country code + number)
    - [ ] Email-Id
    - [ ] State + Pincode
    - [ ] Address
  - **Right column:**
    - [ ] Hiring Date (date picker)
    - [ ] Government ID Type (dropdown)
    - [ ] Upload Files area (ID & Offer Letter) with drag-and-drop
- [ ] "Add New Employee" button (update label to "Add New Payment")
- [ ] Submit to POST `/payments`

### 15.2 — Edit Payment Modal
> **Design:** Same layout as Add. Title "Edit Payment". "Add New Employee" button (update label).

- [ ] Pre-populate all fields
- [ ] "Save Changes" / update button
- [ ] Submit to PUT `/payments/:id`
- [ ] Commit: `"feat: payment page with add/edit modals"`

---

## 🧾 PHASE 16: Invoice Page (`/invoice`)
> **Design:** Stats cards (Active: sky-blue `bg-primary-500`, Inactive, Deleted) + "Active Invoice" table. Columns: Employee Name (with avatar), Employee ID, Department ID, Hire Date, Email-Id (truncated), Phone Number, Actions.

- [ ] Add page header "Invoice" with `← Back` link
- [ ] Build 3 stats cards
- [ ] Add "Active Invoice" section heading
- [ ] Build toolbar: Search, Filters, "⊕ Add New Employee" (update label), Export
- [ ] Build table with columns:
  - [ ] Checkbox
  - [ ] Employee Name (with avatar)
  - [ ] Employee ID
  - [ ] Department ID
  - [ ] Hire Date (date + time)
  - [ ] Email-Id (truncated with `…`)
  - [ ] Phone Number
  - [ ] Actions: Edit + Delete + View icons
- [ ] Implement pagination
- [ ] Add invoice generation action (download PDF button in row actions)
- [ ] Commit: `"feat: invoice page with list view and download action"`

---

## 🔔 PHASE 17: Global UI Components & UX Polish

### 17.1 — Reusable Components
- [ ] `<StatsCard>` — sky-blue `bg-primary-500` (active) or white variant, metric, trend badge
- [ ] `<DataTable>` — generic table with checkbox, pagination, sortable columns
- [ ] `<StatusBadge>` — colored pill for: Drafted, Pending, Received, Rejected, In Transit, Delivered, Delayed, RTO, Re-attempt
- [ ] `<ConfirmDialog>` — reusable Yes/No confirmation with orange warning icon
- [ ] `<TimeFilterBar>` — 1d 7d 1m 3m 6m 1y 3y 5y toggle group + Select dates
- [ ] `<PhoneInput>` — country code dropdown + number input (US +1 default)
- [ ] `<FileUpload>` — drag-and-drop area, accepts SVG/PNG/JPG/PDF, max 2MB
- [ ] `<AddressTooltip>` — truncated address text with `…` + hover tooltip
- [ ] `<AvatarStack>` — overlapping circular avatars + `+N` overflow badge
- [ ] `<SearchInput>` — input with magnifier icon
- [ ] `<ExportButton>` — sky-blue primary button (`bg-primary-500`) with download icon
- [ ] `<BulkUploadButton>` — outlined button with upload icon

### 17.2 — Loading & Error States
- [ ] Add skeleton loaders for all table rows (shimmer effect)
- [ ] Add skeleton loaders for all stats cards
- [ ] Add skeleton loaders for all dashboard charts
- [ ] Implement `<ErrorBoundary>` on all page-level components
- [ ] Add empty state component (centered icon + message) for empty tables
- [ ] Show inline field validation errors in all forms (red border + error text)
- [ ] Display toast notifications for all CRUD operations (success + error)

### 17.3 — Accessibility & Responsiveness
- [ ] Ensure all icon-only buttons have `aria-label`
- [ ] Ensure all form inputs have proper `<label>` elements
- [ ] Maintain 4.5:1 color contrast ratio throughout
- [ ] Never remove `outline-none` without providing `ring-2 ring-primary-500` alternative
- [ ] Make sidebar collapsible on tablet (<1024px)
- [ ] Make sidebar a full overlay drawer on mobile (<768px)
- [ ] Ensure all modals are scrollable on small screens
- [ ] Ensure tables are horizontally scrollable on mobile

### 17.4 — Animations & Interactions
- [ ] Apply `animate-slide-up` on dashboard widget mount
- [ ] Apply `duration-200 ease-in-out` on all hover state transitions
- [ ] Apply `hover:scale-[1.02]` on KPI cards
- [ ] Apply `ring-2 ring-primary-500 ring-offset-2` on all focused inputs
- [ ] Smooth modal open/close transitions (fade + scale)

---

## 🎯 MILESTONE CHECKLIST

### Pages (15 total):
- [ ] 1. Landing Page
- [ ] 2. Login Page
- [ ] 3. Onboarding Page
- [ ] 4. Dashboard
- [ ] 5. Products
- [ ] 6. Supplier
- [ ] 7. Category
- [ ] 8. Warehouse
- [ ] 9. Stock
- [ ] 10. Purchase Order
- [ ] 11. Sales Order
- [ ] 12. Shipment
- [ ] 13. Customer
- [ ] 14. Employee
- [ ] 15. Department
- [ ] 16. Payment
- [ ] 17. Invoice

### Quality Gates:
- [ ] All pages fully responsive and mobile-friendly
- [ ] Complete error handling and loading states on every page
- [ ] All forms validated before submission
- [ ] All CRUD operations integrated with backend API
- [ ] All modals accessible and keyboard-navigable
- [ ] Consistent design tokens used throughout (no hardcoded colors)
- [ ] All status badges match design color system
- [ ] Sidebar navigation active state highlights correct page

---

*Design System: Tailwind CSS v4 + Shadcn/UI | Brand: MoeWare | Theme: Blue Sky*
