// ========================================
// CATEGORY TYPES
// ========================================

// Parent category reference (as returned by API - lighter than full Category)
export interface ParentCategory {
  id: number
  name: string
  code: string
  fullPath?: string
}

// Full Category type (as returned by API)
export interface Category {
  id: number
  name: string
  code: string
  description?: string
  parentCategoryId?: number
  parentCategory?: ParentCategory | null
  level: number
  fullPath?: string
  childCount?: number
  productCount?: number
  childCategoryCount?: number
  isRootCategory?: boolean
  children?: Category[]
  createdAt?: string
  updatedAt?: string
}

// Category list item (for dropdowns)
export interface CategoryListItem {
  id: number
  name: string
  code: string
  fullPath?: string
  level?: number  // Optional - not always returned by API
  productCount?: number
  hasChildren?: boolean
}

// Category tree node (for hierarchical responses)
export interface CategoryTreeNode {
  id: number
  name: string
  code: string
  description?: string
  level: number
  productCount: number
  children: CategoryTreeNode[]
}
