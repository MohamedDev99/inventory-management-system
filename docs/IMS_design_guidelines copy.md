# 🎨 Design Guidelines - MoeWare Inventory Management System

## Frontend Design System (Tailwind CSS v4)

**Version:** 1.1.0 (Blue Sky Update)  
**Last Updated:** January 27, 2026  
**Design Philosophy:** Clean, modern, intuitive - Technology doesn't have to feel like a different language

---

## 1. Tailwind v4 Configuration

In Tailwind v4, we use a CSS-first configuration. This replaces the old `tailwind.config.js`.

```css
@import "tailwindcss";

@theme {
   /* ========================================
     PRIMARY COLORS - Blue Sky
     ======================================== */
  /* Blue Sky Primary Palette */
  --color-primary-50: #f0f9ff;
  --color-primary-100: #e0f2fe;
  --color-primary-200: #bae6fd;
  --color-primary-300: #7dd3fc;
  --color-primary-400: #38bdf8;
  --color-primary-500: #0ea5e9;
  --color-primary-600: #0284c7;
  --color-primary-700: #0369a1;
  --color-primary-800: #075985;
  --color-primary-900: #0c4a6e;
  --color-primary-DEFAULT: var(--color-primary-500);

  /* ========================================
     ACCENT COLORS - Professional Slate
     ======================================== */
  /* Professional Slate Accents */
  --color-accent-50: #f8fafc;
  --color-accent-100: #f1f5f9;
  --color-accent-200: #e2e8f0;
  --color-accent-500: #64748b;
  --color-accent-900: #0f172a;

/* ========================================
     SEMANTIC COLORS - Success (Green)
     ======================================== */
  --color-success-50: #f0fdf4;
  --color-success-100: #dcfce7;
  --color-success-200: #bbf7d0;
  --color-success-300: #86efac;
  --color-success-400: #4ade80;
  --color-success-500: #22c55e;
  --color-success-600: #16a34a;
  --color-success-700: #15803d;
  --color-success-800: #166534;
  --color-success-900: #14532d;
  --color-success-950: #052e16;

  /* ========================================
     SEMANTIC COLORS - Warning (Orange)
     ======================================== */
  --color-warning-50: #fff7ed;
  --color-warning-100: #ffedd5;
  --color-warning-200: #fed7aa;
  --color-warning-300: #fdba74;
  --color-warning-400: #fb923c;
  --color-warning-500: #f97316;
  --color-warning-600: #ea580c;
  --color-warning-700: #c2410c;
  --color-warning-800: #9a3412;
  --color-warning-900: #7c2d12;
  --color-warning-950: #431407;

  /* ========================================
     SEMANTIC COLORS - Error (Red)
     ======================================== */
  --color-error-50: #fef2f2;
  --color-error-100: #fee2e2;
  --color-error-200: #fecaca;
  --color-error-300: #fca5a5;
  --color-error-400: #f87171;
  --color-error-500: #ef4444;
  --color-error-600: #dc2626;
  --color-error-700: #b91c1c;
  --color-error-800: #991b1b;
  --color-error-900: #7f1d1d;
  --color-error-950: #450a0a;


  /* Typography */
  --font-sans: "Inter", "system-ui", sans-serif;
  --font-display: "Poppins", "Inter", sans-serif;
  --font-mono: "JetBrains Mono", "Fira Code", monospace;

  /* Custom Animations */
  --animate-slide-up: slide-up 0.3s ease-out;
  --animate-fade-in: fade-in 0.2s ease-in;

  @keyframes slide-up {
    0% { transform: translateY(10px); opacity: 0; }
    100% { transform: translateY(0); opacity: 1; }
  }
}

/* Manual Dark Mode variables if needed */
.dark {
  --color-primary-500: #0284c7;
  --color-accent-50: #0f172a;
  --color-accent-900: #f8fafc;
}
```

---

## 2. Brand Identity

* **Brand Name:** MoeWare
* **Tagline:** "Technology doesn't have to feel like a different language"

**Logo Component Example:**
```tsx
export function Logo({ className }: { className?: string }) {
  return (
    <div className={cn("flex items-center gap-2", className)}>
      <div className="bg-gradient-to-br from-primary-600 to-primary-400 rounded-lg p-2">
        <Package className="w-6 h-6 text-white" />
      </div>
      <span className="font-display text-xl font-bold bg-gradient-to-r from-primary-700 to-primary-500 bg-clip-text text-transparent">
        MoeWare
      </span>
    </div>
  );
}
```

---

## 3. Color System (Blue Sky)

* **Primary (Sky Blue):** Used for brand identity, primary buttons, and active states.
* **Success (Green):** Used for "In Stock" labels, successful transactions, and positive growth.
* **Warning (Orange):** Used for "Low Stock" alerts and pending items.
* **Error (Red):** Used for "Out of Stock" alerts and destructive actions.
* **Neutral (Slate):** Used for backgrounds, borders, and secondary text.

---

## 4. Typography

* **H1 - Page Titles:** `text-4xl font-bold tracking-tight`
* **Body Text:** `text-base font-normal leading-normal text-slate-700`
* **Data/Metrics:** `font-mono font-bold tabular-nums` (Ensures numbers align in tables).

---

## 5. Layout & Spacing

* **Container:** Standardized at `max-w-7xl mx-auto px-4`.
* **Sidebar:** Fixed at `280px` for desktop navigation.
* **Grid Gaps:** Use `gap-6` (24px) for dashboard cards and `gap-4` for form fields.

---

## 6. Components

> **Note:** This project uses **Shadcn/UI** for standard components (Buttons, Inputs, Forms, Tables, Modals, Alerts, Badges, etc.). 
> 
> See [Shadcn/UI Documentation](https://ui.shadcn.com) for these components.
>
> Below are **custom components** specific to the Inventory Management System.

### Using Shadcn/UI Components

```bash
# Install Shadcn/UI components as needed
npx shadcn-ui@latest add button
npx shadcn-ui@latest add input
npx shadcn-ui@latest add table
npx shadcn-ui@latest add dialog
npx shadcn-ui@latest add alert
npx shadcn-ui@latest add badge
npx shadcn-ui@latest add select
npx shadcn-ui@latest add textarea
npx shadcn-ui@latest add checkbox
npx shadcn-ui@latest add card
npx shadcn-ui@latest add toast
```

---

## 7. States & Interactions

* **Hover:** Subtle brightness increase or `1.02` scale.
* **Focus:** `ring-2 ring-primary-500 ring-offset-2`.
* **Empty States:** Use a centered slate-colored icon with a `text-slate-400` message.

---

## 8. Responsive Design

* **Mobile First:** All layouts start as single-column.
* **Breakpoints:**
    - `sm`: (640px) Cards stack 1-per-row.
    - `md`: (768px) Dashboard grids move to 2-columns.
    - `lg`: (1024px) Sidebar becomes visible.

---

## 9. Accessibility

* **Contrast:** Maintain 4.5:1 ratio for all body text.
* **Focus States:** Never remove `outline-none` without providing a `ring` alternative.
* **Screen Readers:** Use `aria-label` for icon-only buttons.

---

## 10. Animation & Motion

* **Transitions:** Use `duration-200 ease-in-out` for all hover states.
* **Page Loads:** Use the custom `animate-slide-up` for dashboard widgets to create a premium feel.

---

## Changelog

### Version 1.1.0 (January 27, 2026)
- **Rebrand:** Changed system name from The UnityWare to **MoeWare**.
- **Color System:** Migrated from Purple to **Blue Sky**.
- **Tech Stack:** Updated configuration for **Tailwind CSS v4** (@theme blocks).
- **Architecture:** Integrated **Shadcn/UI** as the primary component library.
- **Cleanup:** Removed deprecated Iconography, Navigation, and Data Visualization sections.

### Version 1.0.0 (January 26, 2026)
- Initial release with Purple theme and Tailwind v3.