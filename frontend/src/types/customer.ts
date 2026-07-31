// ========================================
// CUSTOMER TYPES
// ========================================

// Light reference type (as returned by API in list views)
export interface CustomerRef {
  // NOTE: This is a partial/reference type used in list views
  // Full Customer type is defined below with all details
  id: number
  customerCode: string
  contactName: string
  // Additional fields may be available in detail views
}

// Customer address as simple strings (as returned by API)
export interface CustomerAddress {
  address: string
  city: string
  state: string
  country: string
  postalCode: string
}

export interface CustomerStats {
  totalOrders: number
  totalSpent: number
  averageOrderValue: number
}

// Full Customer type (as returned by API)
export interface Customer {
  id: number
  customerCode: string
  companyName?: string
  contactName: string
  email?: string
  phone?: string
  mobile?: string
  // API returns string for address fields, not CustomerAddress object
  billingAddress?: string
  billingCity?: string
  billingState?: string
  billingCountry?: string
  billingPostalCode?: string
  shippingAddress?: string
  shippingCity?: string
  shippingState?: string
  shippingCountry?: string
  shippingPostalCode?: string
  customerType?: "RETAIL" | "WHOLESALE" | "DISTRIBUTOR" | "CORPORATE"
  paymentTerms?: string
  creditLimit?: number
  outstandingBalance?: number
  taxId?: string
  isActive: boolean
  stats?: CustomerStats
  createdAt: string
  updatedAt?: string
  version?: number
}

// Form data type (for creating/updating customers)
export interface CustomerFormData {
  customerCode: string
  companyName?: string
  contactName: string
  email?: string
  phone?: string
  mobile?: string
  billingAddress?: string
  billingCity?: string
  billingState?: string
  billingCountry?: string
  billingPostalCode?: string
  shippingAddress?: string
  shippingCity?: string
  shippingState?: string
  shippingCountry?: string
  shippingPostalCode?: string
  customerType?: "RETAIL" | "WHOLESALE" | "DISTRIBUTOR" | "CORPORATE"
  paymentTerms?: string
  creditLimit?: number
  taxId?: string
  isActive?: boolean
}
