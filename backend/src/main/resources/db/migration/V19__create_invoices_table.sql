-- V13: Create invoices table
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    sales_order_id BIGINT NOT NULL REFERENCES sales_orders(id) ON DELETE RESTRICT,
    customer_id BIGINT NOT NULL REFERENCES customers(id) ON DELETE RESTRICT,
    invoice_date DATE NOT NULL,
    due_date DATE NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    tax_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL,
    paid_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    balance_due DECIMAL(12,2) NOT NULL,
    invoice_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' CHECK (invoice_status IN ('DRAFT', 'SENT', 'PAID', 'PARTIAL', 'OVERDUE', 'CANCELLED')),
    payment_terms VARCHAR(100),
    notes TEXT,
    file_url VARCHAR(500),
    generated_by BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT chk_invoice_amounts CHECK (total_amount >= paid_amount AND balance_due = total_amount - paid_amount)
);

CREATE INDEX idx_invoice_sales_order ON invoices(sales_order_id);
CREATE INDEX idx_invoice_customer ON invoices(customer_id);
CREATE INDEX idx_invoice_status ON invoices(invoice_status);
CREATE INDEX idx_invoice_date ON invoices(invoice_date DESC);
CREATE INDEX idx_invoice_due_date ON invoices(due_date);
CREATE INDEX idx_invoice_unpaid ON invoices(invoice_status, due_date)
    WHERE invoice_status IN ('SENT', 'PARTIAL', 'OVERDUE');