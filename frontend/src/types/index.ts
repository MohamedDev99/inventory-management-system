export interface User {
  id: string;
  username: string;
  email: string;
  role: 'ADMIN' | 'MANAGER' | 'WAREHOUSE_STAFF' | 'VIEWER';
}

export interface Product {
  id: string;
  sku: string;
  name: string;
  description: string;
  category: string;
  unit: string;
  reorderLevel: number;
  minStock: number;
}

export interface Warehouse {
  id: string;
  name: string;
  location: string;
  capacity: number;
}

// Add more types as needed