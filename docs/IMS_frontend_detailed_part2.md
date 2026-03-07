# 📋 IMS Frontend — Detailed Development Checklist (Part 2)
**Project:** MoeWare Inventory Management System  
**Design System:** Tailwind CSS v4 + Shadcn/UI  
**Stack:** React, TypeScript, Recharts, React Router  
**Theme:** Blue Sky (`primary-500: #0ea5e9`) | Brand tagline: *"Technology doesn't have to feel like a different language"*

> This is Part 2, covering: **Reports**, **Notifications System**, and **Settings**. These pages have no screenshot designs — layout and UX are defined here based on the API contracts and design system guidelines.

---

## 📑 PHASE 18: Reports Page (`/reports`)

> **Design Approach:** Two-section layout. Top: report type selection cards (grid). Bottom: generated reports history table. A slide-in or modal panel appears to configure parameters before generating a report. Consistent with the rest of the app — `← Back` header, Blue Sky primary buttons, Shadcn/UI components throughout.

### 18.1 — Reports Page Layout & Header

- [ ] Add page header "Reports" with `← Back` link
- [ ] Add page subtitle: *"Generate, schedule, and download reports for your inventory data"*
- [ ] Build two-tab or two-section layout:
  - [ ] **Tab 1: Generate Report** — report type cards grid
  - [ ] **Tab 2: Report History** — generated reports table
- [ ] Add "Scheduled Reports" button (top-right, outlined) to open scheduled reports drawer
- [ ] Use `<TimeFilterBar>` on the Report History tab (1d 7d 1m 3m 6m 1y 3y 5y)

---

### 18.2 — Report Type Cards Grid

- [ ] Fetch available report types from `GET /api/v1/reports/types`
- [ ] Render each report type as a card in a 3-column responsive grid (2-col on tablet, 1-col on mobile):
  - [ ] **Stock Valuation Report**
    - [ ] Icon: warehouse/box icon (`text-primary-500`)
    - [ ] Title: "Stock Valuation Report"
    - [ ] Description: "Complete inventory valuation by warehouse and category"
    - [ ] Supported formats badge row: `PDF` `EXCEL` `CSV`
    - [ ] "Generate →" button (sky-blue primary, `bg-primary-500`)
  - [ ] **Inventory Movement History**
    - [ ] Icon: arrows/transfer icon
    - [ ] Title: "Inventory Movement History"
    - [ ] Description: "Detailed history of all inventory movements including transfers and adjustments"
    - [ ] Supported formats: `PDF` `EXCEL` `CSV`
    - [ ] "Generate →" button
  - [ ] **Sales Analysis Report**
    - [ ] Icon: chart/line icon
    - [ ] Title: "Sales Analysis Report"
    - [ ] Description: "Sales performance by product, customer, category, and period"
    - [ ] Supported formats: `PDF` `EXCEL`
    - [ ] "Generate →" button
  - [ ] **Purchase Order History**
    - [ ] Icon: shopping cart / purchase icon
    - [ ] Title: "Purchase Order History"
    - [ ] Description: "Complete purchase order history with supplier performance metrics"
    - [ ] Supported formats: `PDF` `EXCEL`
    - [ ] "Generate →" button
  - [ ] **Low Stock Alert Report**
    - [ ] Icon: warning/alert icon (`text-warning-500`)
    - [ ] Title: "Low Stock Alert Report"
    - [ ] Description: "All products currently below their reorder level, with restock suggestions"
    - [ ] Supported formats: `PDF` `EXCEL` `CSV`
    - [ ] "Generate →" button
- [ ] Add hover effect on cards: `hover:shadow-md hover:border-primary-300 transition duration-200`
- [ ] Add `cursor-pointer` and `role="button"` for accessibility

---

### 18.3 — Generate Report Configuration Modal/Panel

> Opens when user clicks "Generate →" on any report type card. Shows dynamic fields based on the selected report type.

- [ ] Build `<GenerateReportModal>` component (Shadcn Dialog)
- [ ] Modal header: report type name + close (`×`) button
- [ ] Show report type description as subtitle inside modal

**Common fields (all report types):**
- [ ] Report Name (text input, pre-filled with suggested name e.g., "Stock Valuation Report — Feb 2026")
- [ ] Description (textarea, optional)
- [ ] Output Format selection (toggle button group): `PDF` | `EXCEL` | `CSV` (only show formats supported by the selected type)
- [ ] Date Range picker (Start Date + End Date) — shown for types that require it:
  - [ ] Stock Valuation, Movement History, Sales Analysis, Purchase History

**Type-specific fields:**

- [ ] **Stock Valuation** extra fields:
  - [ ] Warehouse(s) multi-select dropdown (optional, default: all warehouses)
  - [ ] Valuation Type radio: `COST` | `RETAIL`
  - [ ] Include Inactive Products toggle (default: off)

- [ ] **Inventory Movement History** extra fields:
  - [ ] Product(s) multi-select dropdown (optional, default: all)
  - [ ] Movement Type multi-select: `TRANSFER` | `ADJUSTMENT` | `PURCHASE_RECEIPT` | `SALE`

- [ ] **Sales Analysis** extra fields:
  - [ ] Group By dropdown: `Product` | `Customer` | `Category` | `Warehouse`

- [ ] **Purchase History** extra fields:
  - [ ] Supplier(s) multi-select dropdown (optional)
  - [ ] Status multi-select: `DRAFTED` | `PENDING` | `IN_TRANSIT` | `RECEIVED` | `REJECTED`

- [ ] **Low Stock Alert** extra fields:
  - [ ] Warehouse(s) multi-select dropdown (optional)
  - [ ] Category(ies) multi-select dropdown (optional)

**Modal footer:**
- [ ] "Cancel" button (outlined, closes modal)
- [ ] "Generate Report" button (sky-blue primary, `bg-primary-500`)
- [ ] On submit: call `POST /api/v1/reports/generate`
- [ ] After submission: show inline success notice inside modal — *"Report generation started. You'll be notified when it's ready."*
- [ ] Auto-close modal after 2 seconds, redirect user to Report History tab
- [ ] Show error message inline (below form) if API returns error

---

### 18.4 — Report Generation Progress

- [ ] After triggering generation, add a "Generating…" row at top of Report History table with a spinner and status badge `Pending`
- [ ] Poll `GET /api/v1/reports/{id}` every 5 seconds while status is `PENDING`
- [ ] When status changes to `COMPLETED`:
  - [ ] Update row status badge to `Completed` (green)
  - [ ] Show download button in row Actions
  - [ ] Trigger a toast notification: *"Report ready — click to download"*
- [ ] When status is `FAILED`:
  - [ ] Update row status badge to `Failed` (red)
  - [ ] Show retry icon button in row Actions
  - [ ] Show error toast: *"Report generation failed. Please try again."*

---

### 18.5 — Report History Table (Tab 2)

- [ ] Fetch reports list from `GET /api/v1/reports` (paginated)
- [ ] Build toolbar: Search input, Filters button (filter by type and status), `<TimeFilterBar>`
- [ ] Build table with columns:
  - [ ] Report Name
  - [ ] Report Type (badge/tag: `STOCK_VALUATION`, `SALES_ANALYSIS`, etc.)
  - [ ] Generated By (username)
  - [ ] Format (PDF / EXCEL / CSV badge)
  - [ ] File Size (formatted: "2.4 MB")
  - [ ] Status badge: `Pending` (orange) | `Completed` (green) | `Failed` (red)
  - [ ] Generated At (date + time)
  - [ ] Expires At (date + time, red text if expired or expiring within 3 days)
  - [ ] Actions:
    - [ ] Download icon (active only when `COMPLETED`) — calls `GET /api/v1/reports/{id}/download`
    - [ ] Delete icon (trash, red) — calls `DELETE /api/v1/reports/{id}`
    - [ ] Retry icon (shown only when `FAILED`) — re-opens Generate modal pre-filled with same params
- [ ] Apply row highlight (`bg-warning-50`) for reports expiring within 3 days
- [ ] Apply row highlight (`bg-error-50`) for expired reports
- [ ] Implement pagination: Previous | 1 2 3 … | Next
- [ ] Show empty state when no reports: centered icon + *"No reports generated yet. Click 'Generate →' on a report type to get started."*

---

### 18.6 — Scheduled Reports Drawer

> Opened via "Scheduled Reports" button in page header. Slide-in drawer from the right.

- [ ] Build `<ScheduledReportsDrawer>` (Shadcn Sheet component, right side)
- [ ] Drawer header: "Scheduled Reports" + close button
- [ ] Fetch scheduled reports from `GET /api/v1/reports/scheduled`
- [ ] Render each scheduled report as a card:
  - [ ] Report name + type badge
  - [ ] Frequency: `DAILY` | `WEEKLY` | `MONTHLY`
  - [ ] Schedule detail (e.g., "Every Monday at 08:00")
  - [ ] Recipients list (email chips)
  - [ ] Format badge (PDF/EXCEL)
  - [ ] Enabled/Disabled toggle switch
  - [ ] Delete (trash) icon button
- [ ] Show empty state if no scheduled reports
- [ ] Add "⊕ Schedule New Report" button at bottom of drawer:
  - [ ] Opens `<ScheduleReportModal>` (separate modal on top of drawer)
  - [ ] Fields:
    - [ ] Report Type (dropdown, all types)
    - [ ] Report Name
    - [ ] Frequency: `Daily` | `Weekly` | `Monthly` (radio group)
    - [ ] Day of Week (shown only when Weekly — dropdown: Monday–Sunday)
    - [ ] Day of Month (shown only when Monthly — number input 1–28)
    - [ ] Time (time picker, e.g., "08:00")
    - [ ] Output Format: `PDF` | `EXCEL` | `CSV`
    - [ ] Recipients (email tag input, add multiple emails)
    - [ ] Enabled toggle (default: on)
  - [ ] "Schedule Report" button (sky-blue primary)
  - [ ] Submit to `POST /api/v1/reports/schedule`
  - [ ] Show success toast: *"Report scheduled successfully"*
- [ ] Commit: `"feat: reports page with generation, history table, and scheduling"`

---

## 🔔 PHASE 19: Notifications System

> The Notifications system has two surfaces: (1) a **Notification Bell** in the sidebar/top bar (always visible), and (2) a full **Notifications Center Page** (`/notifications`). Both are driven by the same API.

---

### 19.1 — Notification Bell (Global, Sidebar)

- [ ] Add bell icon button to the top area of the sidebar (above nav items) or in the top bar
- [ ] Fetch unread count from `GET /api/v1/notifications/unread` on app load and every 60 seconds
- [ ] Display unread count as a red badge on the bell icon:
  - [ ] Show exact count up to 99
  - [ ] Show `99+` if count exceeds 99
  - [ ] Hide badge entirely when count is 0
- [ ] On bell click: open `<NotificationDropdown>` popover (see 19.2)
- [ ] Animate bell icon with a subtle shake animation when a new high-priority notification arrives
- [ ] Use `priority` to color-code badge: red for `CRITICAL`, orange for `HIGH`, default for others

---

### 19.2 — Notification Dropdown Popover

> A compact popover anchored to the bell icon showing the most recent notifications. Max height with scroll.

- [ ] Build `<NotificationDropdown>` using Shadcn Popover
- [ ] Popover width: `400px` (desktop), full-width on mobile
- [ ] Popover header:
  - [ ] "Notifications" title (bold)
  - [ ] Unread count badge (e.g., "12 unread")
  - [ ] "Mark all as read" text button (right-aligned, `text-primary-500`)
- [ ] Fetch latest 10 notifications from `GET /api/v1/notifications?size=10&sort=createdAt,desc`
- [ ] Render each notification item:
  - [ ] Left: type icon (color-coded by `notificationType`):
    - [ ] `LOW_STOCK` → warning triangle (`text-warning-500`)
    - [ ] `ORDER_APPROVED` → checkmark circle (`text-success-500`)
    - [ ] `SHIPMENT` → truck icon (`text-primary-500`)
    - [ ] `STOCK_ADJUSTMENT` → arrows icon (`text-primary-500`)
    - [ ] `SYSTEM` → info circle (`text-accent-500`)
  - [ ] Center: notification title (bold if unread) + message (truncated to 2 lines)
  - [ ] Right: relative time (e.g., "2h ago", "Just now") + unread dot (blue `bg-primary-500`)
  - [ ] Priority indicator: left border color — red for `CRITICAL`, orange for `HIGH`, blue for `MEDIUM`, grey for `LOW`
  - [ ] Unread items: `bg-primary-50` background tint
  - [ ] Read items: white background
  - [ ] On click: mark as read via `PATCH /api/v1/notifications/{id}/read`, navigate to `actionUrl` if present
- [ ] Show "No notifications" empty state when list is empty
- [ ] Popover footer:
  - [ ] "View all notifications →" link (navigates to `/notifications` page)
- [ ] Close popover on outside click or Escape key
- [ ] "Mark all as read" calls `PATCH /api/v1/notifications/read-all`, refreshes list and badge count

---

### 19.3 — Notifications Center Page (`/notifications`)

> Full-page view of all notifications with filtering, bulk actions, and management tools.

- [ ] Add page header "Notifications" with `← Back` link
- [ ] Build 3 stats cards at top:
  - [ ] Total Notifications (count)
  - [ ] Unread (sky-blue `bg-primary-500` card, unread count from `bySeverity` + `byType` breakdown)
  - [ ] Critical Alerts (red `bg-error-500` card, CRITICAL count)

**Toolbar:**
- [ ] Search input (search by title or message text)
- [ ] Filter by Type dropdown: All | Low Stock | Order Approved | Order Received | Shipment | Stock Adjustment | System
- [ ] Filter by Priority dropdown: All | Critical | High | Medium | Low
- [ ] Filter by Status toggle: All | Unread | Read
- [ ] Date range filter (Start Date — End Date)
- [ ] "Mark all as read" button (outlined)
- [ ] "Clear read" button (outlined, red text) — calls `DELETE /api/v1/notifications/clear-read`

**Notifications List:**
- [ ] Fetch from `GET /api/v1/notifications` (paginated, 20 per page)
- [ ] Render each notification as a full-width row card:
  - [ ] Checkbox (for bulk selection)
  - [ ] Left: type icon (color-coded, same as dropdown)
  - [ ] Priority left-border color indicator (red/orange/blue/grey)
  - [ ] Title (bold if unread) + message (full text, not truncated)
  - [ ] Reference link if `referenceType` is present (e.g., "View Product →", "View Purchase Order →")
  - [ ] Bottom meta: notification type badge + priority badge + timestamp (full date + time)
  - [ ] Right: unread blue dot | "Mark as read" / "Mark as unread" toggle icon | Delete (trash) icon
  - [ ] Unread row: `bg-primary-50` tint
- [ ] Implement bulk actions bar (appears when one or more checkboxes selected):
  - [ ] "Mark selected as read" button
  - [ ] "Mark selected as unread" button
  - [ ] "Delete selected" button (red)
  - [ ] Selected count label (e.g., "3 selected")
- [ ] Implement pagination
- [ ] Show empty state when no notifications match filters

---

### 19.4 — Notification Preferences (within Settings — see Phase 20)

> Notification preferences are managed inside the Settings page, not as a standalone page.
> API: `GET /api/v1/notifications/preferences` and `PUT /api/v1/notifications/preferences`

*(See Phase 20.3 — Notification Preferences Settings tab)*

- [ ] Commit: `"feat: notifications bell, dropdown, and full notifications center page"`

---

## ⚙️ PHASE 20: Settings Page (`/settings`)

> **Design Approach:** Settings page uses a left-tab navigation layout. Tabs on the left, content panel on the right. Consistent with app design system — no screenshots, follows MoeWare Blue Sky design guidelines. All sections separated by section headings with thin dividers.

---

### 20.1 — Settings Page Shell

- [ ] Add page header "Settings" with `← Back` link
- [ ] Build two-column layout:
  - [ ] Left: vertical tab navigation list (`240px` wide, sticky)
  - [ ] Right: tab content panel (scrollable)
- [ ] Build settings tab list (left nav):
  - [ ] 👤 Profile
  - [ ] 🔒 Security
  - [ ] 🔔 Notifications
  - [ ] 🎨 Appearance
  - [ ] 🌐 Language & Region
  - [ ] 🏢 Business Settings *(Admin only)*
  - [ ] 👥 User Management *(Admin only)*
  - [ ] 🗂️ Audit Log *(Admin only)*
- [ ] Highlight active tab with `bg-primary-50 text-primary-600 font-semibold border-l-2 border-primary-500`
- [ ] Collapse left tabs to icon-only on mobile (<768px), show full labels on desktop
- [ ] Each tab content section starts with a section title + description subtitle

---

### 20.2 — Profile Settings Tab

- [ ] Section heading: "Profile" + subtitle: "Update your personal information and avatar"
- [ ] **Avatar section:**
  - [ ] Display current avatar (circular, 96px)
  - [ ] "Change Photo" button below avatar
  - [ ] File upload input (accepts: JPG, PNG, max 2MB)
  - [ ] Preview new image before saving
  - [ ] "Remove Photo" link (red text, sets avatar to initials fallback)
- [ ] **Personal Information form:**
  - [ ] Full Name (text input)
  - [ ] Username (text input, read-only or editable)
  - [ ] Email (text input, with "Verified" green badge or "Unverified" warning badge)
  - [ ] Phone Number (country code dropdown + number input)
  - [ ] Job Title / Role (text input, read-only — set by admin)
- [ ] **Address section:**
  - [ ] State (select dropdown)
  - [ ] Pincode (text input)
  - [ ] Address (full-width text area)
- [ ] "Save Changes" button (sky-blue primary, `bg-primary-500`) — bottom of form
- [ ] Show success toast: *"Profile updated successfully"*
- [ ] Show inline field errors on validation failure

---

### 20.3 — Security Settings Tab

- [ ] Section heading: "Security" + subtitle: "Manage your password and account security"
- [ ] **Change Password section:**
  - [ ] Current Password (password input with show/hide toggle)
  - [ ] New Password (password input with show/hide toggle)
  - [ ] Confirm New Password (password input with show/hide toggle)
  - [ ] Password strength indicator bar (weak / fair / strong / very strong)
  - [ ] Password requirements checklist (shown below input):
    - [ ] ✓ At least 8 characters
    - [ ] ✓ One uppercase letter
    - [ ] ✓ One number
    - [ ] ✓ One special character
  - [ ] "Update Password" button (sky-blue primary)
  - [ ] Show success toast: *"Password updated successfully. Please log in again."*
  - [ ] On success: clear form + redirect to login after 2 seconds
- [ ] **Active Sessions section:**
  - [ ] Section subheading: "Active Sessions"
  - [ ] List all active sessions (device, browser, IP address, last active timestamp)
  - [ ] Current session labeled with "Current" green badge
  - [ ] "Revoke" button on each non-current session
  - [ ] "Revoke all other sessions" danger button (outlined red)
- [ ] **Two-Factor Authentication (2FA) placeholder section:**
  - [ ] Toggle: Enable 2FA (disabled state with "Coming Soon" badge)
  - [ ] Greyed out, not interactive yet

---

### 20.4 — Notification Preferences Settings Tab

- [ ] Section heading: "Notifications" + subtitle: "Control how and when you receive notifications"
- [ ] Fetch preferences from `GET /api/v1/notifications/preferences`
- [ ] **Global Channels section:**
  - [ ] Section subheading: "Notification Channels"
  - [ ] Email Notifications master toggle (on/off)
  - [ ] Push Notifications master toggle (on/off)
  - [ ] SMS Notifications master toggle (on/off, with "US numbers only" hint)
- [ ] **Per-Type Notification Preferences table:**
  - [ ] Section subheading: "Notification Types"
  - [ ] Table with columns: Notification Type | Enabled | Email | Push | SMS
  - [ ] Row for each type:
    - [ ] Low Stock Alerts (`LOW_STOCK`)
    - [ ] Order Approved (`ORDER_APPROVED`)
    - [ ] Order Received (`ORDER_RECEIVED`)
    - [ ] Shipment Updates (`SHIPMENT`)
    - [ ] Stock Adjustments (`STOCK_ADJUSTMENT`)
    - [ ] System Notifications (`SYSTEM`)
  - [ ] Each cell: toggle switch component
  - [ ] Email/Push/SMS toggles disabled (greyed) if their global channel toggle is off
  - [ ] Row enabled toggle: disabling it greys out all channel toggles for that row
- [ ] **Quiet Hours section:**
  - [ ] Section subheading: "Quiet Hours"
  - [ ] Enable Quiet Hours toggle
  - [ ] Start Time time picker (shown when enabled, e.g., "22:00")
  - [ ] End Time time picker (e.g., "08:00")
  - [ ] Helper text: *"During quiet hours, push and SMS notifications will be silenced"*
- [ ] "Save Preferences" button (sky-blue primary, bottom of tab)
- [ ] Submit to `PUT /api/v1/notifications/preferences`
- [ ] Show success toast: *"Notification preferences saved"*

---

### 20.5 — Appearance Settings Tab

- [ ] Section heading: "Appearance" + subtitle: "Customize the look and feel of your workspace"
- [ ] **Theme section:**
  - [ ] Section subheading: "Color Theme"
  - [ ] Theme selector cards (3 options in a row):
    - [ ] **Light** — white card preview with light sidebar
    - [ ] **Dark** — dark card preview with dark sidebar
    - [ ] **System** — split card preview (auto-detect OS preference)
  - [ ] Active theme card: `border-2 border-primary-500` ring + checkmark badge
  - [ ] On selection: apply theme immediately (CSS class on `<html>` or `<body>`)
  - [ ] Persist theme preference to `localStorage`
- [ ] **Sidebar Density section:**
  - [ ] Section subheading: "Sidebar Style"
  - [ ] Radio group: `Comfortable` (default, wider spacing) | `Compact` (tighter spacing)
- [ ] **Table Density section:**
  - [ ] Section subheading: "Table Row Height"
  - [ ] Radio group: `Default` | `Compact` | `Spacious`
- [ ] Changes apply immediately (live preview)
- [ ] "Save Appearance" button (sky-blue primary)
- [ ] Persist all appearance settings to `localStorage`

---

### 20.6 — Language & Region Settings Tab

- [ ] Section heading: "Language & Region" + subtitle: "Set your language, timezone, and date preferences"
- [ ] **Language section:**
  - [ ] Language dropdown (searchable select): English (default), with placeholder for future languages
  - [ ] Show "More languages coming soon" hint text below
- [ ] **Timezone section:**
  - [ ] Timezone searchable select (full IANA timezone list, e.g., "America/New_York")
  - [ ] Current local time preview (updates live as timezone changes): *"Current time: 3:45 PM"*
- [ ] **Date & Time Format section:**
  - [ ] Date Format radio: `MM/DD/YYYY` | `DD/MM/YYYY` | `YYYY-MM-DD`
  - [ ] Time Format radio: `12-hour (AM/PM)` | `24-hour`
- [ ] **Currency section:**
  - [ ] Currency select (e.g., USD $, EUR €, GBP £)
  - [ ] Currency position radio: `Before amount ($100)` | `After amount (100$)`
- [ ] "Save Preferences" button (sky-blue primary)
- [ ] Show success toast: *"Language & region preferences saved"*

---

### 20.7 — Business Settings Tab *(Admin only)*

- [ ] Show tab only if user role is `ADMIN`
- [ ] Section heading: "Business Settings" + subtitle: "Configure your company profile and system-wide defaults"
- [ ] **Company Profile section:**
  - [ ] Company / Business Name
  - [ ] Industry (select)
  - [ ] Domain (select)
  - [ ] Product/Service Type (select)
  - [ ] Business Type radio: `Super-stockiest` | `Distributor` | `Retailer` | `Brand`
  - [ ] Company Logo upload (drag-and-drop, 200×200px recommended, JPG/PNG max 2MB)
- [ ] **SKU & Inventory Defaults section:**
  - [ ] Default SKU Size range (radio matching onboarding options)
  - [ ] Low Stock Warning Threshold % (number input, e.g., "10%")
  - [ ] Auto-reorder toggle (default: off)
  - [ ] Default valuation method: `COST` | `RETAIL` (radio)
- [ ] **Tax & Pricing section:**
  - [ ] Default Tax Rate % (number input)
  - [ ] Tax inclusion toggle: `Prices include tax` | `Tax added at checkout`
- [ ] "Save Business Settings" button (sky-blue primary)
- [ ] Show success toast: *"Business settings updated"*

---

### 20.8 — User Management Tab *(Admin only)*

- [ ] Show tab only if user role is `ADMIN`
- [ ] Section heading: "User Management" + subtitle: "Manage system users and their access roles"
- [ ] Build toolbar: Search input, Filter by Role dropdown, "⊕ Invite New User" button (sky-blue primary)
- [ ] Build users table with columns:
  - [ ] Avatar + Full Name
  - [ ] Email
  - [ ] Role badge: `Admin` (red) | `Manager` (orange) | `Staff` (blue) | `Viewer` (grey)
  - [ ] Status badge: `Active` (green) | `Invited` (yellow, pending) | `Inactive` (grey)
  - [ ] Last Login (date + time, or "Never")
  - [ ] Created At
  - [ ] Actions: Edit role (dropdown inline) + Deactivate/Activate toggle + Delete
- [ ] **Invite New User modal:**
  - [ ] Email input
  - [ ] Role select: `Admin` | `Manager` | `Staff` | `Viewer`
  - [ ] "Send Invite" button (sky-blue primary)
  - [ ] On success: row appears with `Invited` status + toast: *"Invitation sent to [email]"*
- [ ] **Edit User Role inline:**
  - [ ] Click role badge in row → opens dropdown to reassign role
  - [ ] Confirm change with toast: *"[Name]'s role updated to [Role]"*
- [ ] Implement pagination

---

### 20.9 — Audit Log Tab *(Admin only)*

- [ ] Show tab only if user role is `ADMIN`
- [ ] Section heading: "Audit Log" + subtitle: "Track all system actions and changes for compliance and security"
- [ ] Fetch from `GET /api/v1/audit-logs` (paginated)
- [ ] Build toolbar:
  - [ ] Search input (search by action, entity, or user)
  - [ ] Filter by Entity Type: All | Product | Order | Warehouse | User | Category | Stock | Supplier | Customer | Employee | Department | Payment | Invoice
  - [ ] Filter by Action: All | CREATE | UPDATE | DELETE | LOGIN | LOGOUT | EXPORT
  - [ ] Filter by User (searchable dropdown)
  - [ ] Date range picker (Start Date — End Date)
  - [ ] Export Audit Log button (outlined, downloads as CSV)
- [ ] Build audit log table with columns:
  - [ ] Timestamp (date + full time with seconds)
  - [ ] User (avatar + username)
  - [ ] Action badge: `CREATE` (green) | `UPDATE` (blue) | `DELETE` (red) | `LOGIN` (grey) | `LOGOUT` (grey) | `EXPORT` (purple)
  - [ ] Entity Type (e.g., "Product", "Purchase Order")
  - [ ] Entity ID / Name (clickable link navigating to the entity if still exists)
  - [ ] Description / Details (short summary, e.g., "Updated stock level from 100 to 85")
  - [ ] IP Address
  - [ ] View Details icon (eye) → opens side panel showing full before/after JSON diff
- [ ] **Audit Log Detail Side Panel:**
  - [ ] Slide-in from right (Shadcn Sheet)
  - [ ] Shows: full action metadata, entity snapshot before change, entity snapshot after change
  - [ ] Render JSON diff with color-coded added (green) / removed (red) / unchanged lines
- [ ] Implement pagination
- [ ] Commit: `"feat: settings page with profile, security, notifications, appearance, language, business, user management, and audit log tabs"`

---

## 🔁 PHASE 21: Cross-Cutting Concerns & Final Polish

### 21.1 — React Query / Data Fetching Layer

- [ ] Set up `QueryClient` with default stale times:
  - [ ] Dashboard: `staleTime: 30_000` (30s — frequently refreshed)
  - [ ] Notifications unread count: `staleTime: 60_000` (1 min)
  - [ ] Static lists (categories, warehouses): `staleTime: 300_000` (5 min)
- [ ] Set up `queryKeys` constants file (`/constants/queryKeys.ts`) for all entities
- [ ] Implement optimistic updates for mark-as-read on notifications
- [ ] Implement optimistic updates for toggle switches in notification preferences
- [ ] Use `useInfiniteQuery` for notification dropdown (load more on scroll)
- [ ] Implement `refetchOnWindowFocus: true` for notifications and dashboard only

### 21.2 — Global Error Handling

- [ ] Create `<ErrorBoundary>` wrapping each page-level route
- [ ] Create `<ApiErrorFallback>` component (shows error type, message, and "Try Again" button)
- [ ] Handle 401 Unauthorized globally: clear JWT + redirect to `/login` with toast: *"Session expired. Please log in again."*
- [ ] Handle 403 Forbidden: show `<AccessDenied>` page (lock icon + message + "Go to Dashboard" button)
- [ ] Handle 404 Not Found API responses: show inline "Not found" state within the component
- [ ] Handle 500 Server Error: show error boundary fallback with option to report

### 21.3 — Toast Notification System (Global)

- [ ] Configure global toast provider (Shadcn Toaster or react-hot-toast) in `AppLayout`
- [ ] Define toast helper functions in `/utils/toast.ts`:
  - [ ] `toastSuccess(message)` — green checkmark, 3s auto-dismiss
  - [ ] `toastError(message)` — red X, 5s auto-dismiss (longer for errors)
  - [ ] `toastInfo(message)` — blue info icon, 3s auto-dismiss
  - [ ] `toastWarning(message)` — orange warning, 4s auto-dismiss
  - [ ] `toastLoading(message)` — spinner, stays until dismissed programmatically
- [ ] Use `toastLoading` + `toastSuccess`/`toastError` pattern for all async operations (report generation, bulk actions, file uploads)
- [ ] Position toasts: bottom-right on desktop, bottom-center on mobile

### 21.4 — Performance & Accessibility Final Pass

- [ ] Add `React.lazy()` + `<Suspense>` for all page-level components (code splitting)
- [ ] Add `loading="lazy"` on all images
- [ ] Run Lighthouse audit — target: Performance ≥ 85, Accessibility ≥ 95
- [ ] Verify all interactive elements are keyboard-navigable (Tab, Enter, Escape)
- [ ] Verify all modals trap focus correctly (no focus escaping to background)
- [ ] Verify all dropdowns close on Escape key
- [ ] Add `aria-live="polite"` region for toast notifications
- [ ] Add `aria-label` to all icon-only buttons throughout app
- [ ] Test with screen reader (VoiceOver / NVDA) on critical paths: login, add product, generate report
- [ ] Commit: `"feat: final polish, code splitting, accessibility audit"`

---

## 🎯 PART 2 MILESTONE CHECKLIST

### Pages Added in Part 2:
- [ ] 18. Reports — Generate, history table, scheduling
- [ ] 19. Notifications Center — Full list with filters and bulk actions
- [ ] 20. Settings — Profile, Security, Notifications, Appearance, Language, Business, User Management, Audit Log

### Global Systems Added in Part 2:
- [ ] Notification Bell (sidebar, always visible)
- [ ] Notification Dropdown Popover (quick view, top 10)
- [ ] Report generation polling loop
- [ ] React Query caching layer with stale times
- [ ] Global error handling (401/403/500)
- [ ] Toast notification system with typed helpers
- [ ] Audit log with JSON diff viewer

### Quality Gates:
- [ ] All settings tabs persist data correctly via API
- [ ] Notification bell updates in real-time (polling every 60s)
- [ ] Report generation polling stops when status is `COMPLETED` or `FAILED`
- [ ] Admin-only tabs (Business Settings, User Management, Audit Log) are hidden for non-admin users
- [ ] Quiet Hours toggle correctly disables push/SMS channels in preferences
- [ ] Dark mode applies immediately and persists across sessions via `localStorage`
- [ ] All new pages responsive and mobile-friendly
- [ ] Lighthouse Accessibility score ≥ 95 on all new pages

---

*Design System: Tailwind CSS v4 + Shadcn/UI | Brand: MoeWare | Theme: Blue Sky (`primary-500: #0ea5e9`)*  
*Part 1 covers: Landing, Login, Onboarding, Dashboard, Products, Supplier, Category, Warehouse, Stock, Purchase Order, Sales Order, Shipment, Customer, Employee, Department, Payment, Invoice*
