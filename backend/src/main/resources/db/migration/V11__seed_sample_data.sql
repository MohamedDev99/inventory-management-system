-- V11: Seed sample data for development and testing

-- Insert Categories (Hierarchical)
INSERT INTO categories (name, code, parent_category_id, level) VALUES
('Electronics', 'ELEC', NULL, 0),
('Computers', 'COMP', 1, 1),
('Laptops', 'LAPT', 2, 2),
('Desktops', 'DESK', 2, 2),
('Accessories', 'ACCE', 2, 2),
('Mobile Devices', 'MOBI', 1, 1),
('Smartphones', 'SMPH', 6, 2),
('Tablets', 'TABL', 6, 2);

-- Insert Sample Products
INSERT INTO products (sku, name, description, category_id, unit, unit_price, cost_price, reorder_level, min_stock_level, barcode) VALUES
('LAP-001', 'Dell Laptop XPS 15', '15.6" laptop with Intel i7, 16GB RAM', 3, 'PIECE', 1299.99, 899.00, 5, 2, '1234567890123'),
('LAP-002', 'MacBook Pro 14"', '14" laptop with M1 Pro chip, 16GB RAM', 3, 'PIECE', 1999.99, 1499.00, 3, 1, '1234567890124'),
('DESK-001', 'HP Desktop Tower', 'Desktop PC with Intel i5, 8GB RAM', 4, 'PIECE', 799.99, 549.00, 10, 5, '1234567890125'),
('MOUSE-001', 'Logitech MX Master 3', 'Wireless mouse with precision tracking', 5, 'PIECE', 99.99, 65.00, 20, 10, '1234567890126'),
('KEYB-001', 'Mechanical Keyboard RGB', 'Gaming keyboard with RGB lighting', 5, 'PIECE', 149.99, 95.00, 15, 8, '1234567890127'),
('PHONE-001', 'iPhone 14 Pro', '6.1" smartphone with 128GB storage', 7, 'PIECE', 999.99, 749.00, 8, 3, '1234567890128'),
('PHONE-002', 'Samsung Galaxy S23', '6.2" smartphone with 256GB storage', 7, 'PIECE', 899.99, 649.00, 10, 5, '1234567890129'),
('TAB-001', 'iPad Air 2023', '10.9" tablet with 64GB storage', 8, 'PIECE', 599.99, 449.00, 12, 6, '1234567890130');

-- Insert Warehouses (assuming admin user with id=1 exists)
INSERT INTO warehouses (name, code, address, city, state, country, postal_code, manager_id, capacity, is_active) VALUES
('Main Warehouse', 'WH001', '123 Storage Street', 'New York', 'NY', 'USA', '10001', 1, 50000.00, true),
('East Coast Distribution', 'WH002', '456 Logistics Avenue', 'Boston', 'MA', 'USA', '02101', 1, 35000.00, true),
('West Coast Hub', 'WH003', '789 Shipping Boulevard', 'Los Angeles', 'CA', 'USA', '90001', 1, 45000.00, true);

-- Insert Inventory Items
INSERT INTO inventory_items (product_id, warehouse_id, quantity, location_code) VALUES
-- Main Warehouse (WH001)
(1, 1, 50, 'A-12-3'),
(2, 1, 15, 'A-13-2'),
(3, 1, 30, 'B-05-1'),
(4, 1, 100, 'C-08-4'),
(5, 1, 75, 'C-09-2'),
(6, 1, 25, 'D-02-1'),
(7, 1, 40, 'D-03-1'),
(8, 1, 35, 'D-04-2'),

-- East Coast Distribution (WH002)
(1, 2, 30, 'A-01-1'),
(2, 2, 8, 'A-02-1'),
(3, 2, 20, 'B-01-1'),
(4, 2, 60, 'C-01-1'),
(5, 2, 45, 'C-02-1'),
(6, 2, 18, 'D-01-1'),
(7, 2, 25, 'D-02-1'),
(8, 2, 22, 'D-03-1'),

-- West Coast Hub (WH003)
(1, 3, 40, 'A-15-5'),
(2, 3, 12, 'A-16-3'),
(3, 3, 25, 'B-10-2'),
(4, 3, 80, 'C-12-1'),
(5, 3, 55, 'C-13-2'),
(6, 3, 20, 'D-05-3'),
(7, 3, 30, 'D-06-1'),
(8, 3, 28, 'D-07-2');

-- Insert Customers
INSERT INTO customers (customer_code, company_name, contact_name, email, phone, customer_type, payment_terms, is_active) VALUES
('CUST-001', NULL, 'John Doe', 'john.doe@email.com', '+1-555-0100', 'RETAIL', 'COD', true),
('CUST-002', 'ABC Corporation', 'Jane Smith', 'jane.smith@abc.com', '+1-555-0200', 'CORPORATE', 'Net 30', true),
('CUST-003', 'Tech Solutions Inc', 'Bob Johnson', 'bob@techsolutions.com', '+1-555-0300', 'WHOLESALE', 'Net 45', true),
('CUST-004', NULL, 'Alice Williams', 'alice.w@email.com', '+1-555-0400', 'RETAIL', 'Credit Card', true),
('CUST-005', 'XYZ Enterprises', 'Charlie Brown', 'charlie@xyz.com', '+1-555-0500', 'CORPORATE', 'Net 60', true);

-- Insert Departments
INSERT INTO departments (department_code, name, description, cost_center, is_active) VALUES
('DEPT-001', 'Warehouse Operations', 'Manages all warehouse activities', 'CC-WH-001', true),
('DEPT-002', 'Sales', 'Customer sales and order processing', 'CC-SL-001', true),
('DEPT-003', 'Procurement', 'Purchase order management', 'CC-PR-001', true),
('DEPT-004', 'IT', 'Information Technology support', 'CC-IT-001', true),
('DEPT-005', 'Administration', 'General administration', 'CC-AD-001', true);

-- Insert Employees
INSERT INTO employees (employee_code, first_name, last_name, email, phone, job_title, department_id, hire_date, is_active) VALUES
('EMP-001', 'Michael', 'Scott', 'michael.scott@company.com', '+1-555-1001', 'Warehouse Manager', 1, '2023-01-15', true),
('EMP-002', 'Dwight', 'Schrute', 'dwight.schrute@company.com', '+1-555-1002', 'Assistant Manager', 1, '2023-02-01', true),
('EMP-003', 'Jim', 'Halpert', 'jim.halpert@company.com', '+1-555-1003', 'Sales Representative', 2, '2023-03-10', true),
('EMP-004', 'Pam', 'Beesly', 'pam.beesly@company.com', '+1-555-1004', 'Receptionist', 5, '2023-04-20', true),
('EMP-005', 'Stanley', 'Hudson', 'stanley.hudson@company.com', '+1-555-1005', 'Sales Representative', 2, '2023-05-15', true);

-- Update department managers (circular reference handled)
UPDATE departments SET manager_id = 1 WHERE id = 1; -- Warehouse Ops managed by Michael
UPDATE departments SET manager_id = 3 WHERE id = 2; -- Sales managed by Jim
UPDATE departments SET manager_id = 4 WHERE id = 5; -- Admin managed by Pam

-- Update employee managers
UPDATE employees SET manager_id = 1 WHERE id = 2; -- Dwight reports to Michael
UPDATE employees SET manager_id = 3 WHERE id = 5; -- Stanley reports to Jim

COMMENT ON TABLE categories IS 'Sample categories added for development';
COMMENT ON TABLE products IS 'Sample products added for development';
COMMENT ON TABLE warehouses IS 'Sample warehouses added for development';
COMMENT ON TABLE inventory_items IS 'Sample inventory added for development';
COMMENT ON TABLE customers IS 'Sample customers added for development';
COMMENT ON TABLE departments IS 'Sample departments added for development';
COMMENT ON TABLE employees IS 'Sample employees added for development';