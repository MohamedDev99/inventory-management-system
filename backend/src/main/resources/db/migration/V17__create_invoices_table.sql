-- =========================================
-- V17: Create Invoices Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version field for VersionedEntity)
-- Description: Invoice generation and billing with payment tracking and optimistic locking
-- =========================================

-- Create INVOICES table
CREATE TABLE invoices (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    sales_order_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
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
    generated_by BIGINT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_invoice_sales_order FOREIGN KEY (sales_order_id)
        REFERENCES sales_orders(id) ON DELETE RESTRICT,
    CONSTRAINT fk_invoice_customer FOREIGN KEY (customer_id)
        REFERENCES customers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_invoice_generated_by FOREIGN KEY (generated_by)
        REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT chk_invoice_amounts CHECK (
        total_amount >= paid_amount AND
        balance_due = total_amount - paid_amount
    )
);

-- Indexes
CREATE INDEX idx_invoice_sales_order ON invoices(sales_order_id);

CREATE INDEX idx_invoice_customer ON invoices(customer_id);

CREATE INDEX idx_invoice_status ON invoices(invoice_status);

CREATE INDEX idx_invoice_date ON invoices(invoice_date DESC);

CREATE INDEX idx_invoice_due_date ON invoices(due_date);

CREATE INDEX idx_invoice_generated_by ON invoices(generated_by);

CREATE INDEX idx_invoice_customer_date ON invoices(customer_id, invoice_date DESC);

-- Partial index for unpaid/overdue invoices
CREATE INDEX idx_invoice_unpaid ON invoices(invoice_status, due_date)
    WHERE invoice_status IN ('SENT', 'PARTIAL', 'OVERDUE');

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_invoices_updated_at
    BEFORE UPDATE ON invoices
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE invoices IS 'Invoice documents for customer billing and payment tracking';

COMMENT ON COLUMN invoices.invoice_number IS 'Unique invoice number (format: INV-YYYYMMDD-XXXX)';

COMMENT ON COLUMN invoices.invoice_status IS 'Invoice status: DRAFT, SENT, PAID, PARTIAL, OVERDUE, CANCELLED';

COMMENT ON COLUMN invoices.balance_due IS 'Calculated: total_amount - paid_amount';

COMMENT ON COLUMN invoices.file_url IS 'URL to generated PDF invoice in cloud storage';

COMMENT ON COLUMN invoices.payment_terms IS 'Payment terms on this invoice (e.g., Net 30)';

COMMENT ON COLUMN invoices.version IS 'Optimistic lock version - CRITICAL for billing accuracy and payment tracking';

COMMENT ON COLUMN invoices.created_by IS 'Username of the user who created this invoice';

COMMENT ON COLUMN invoices.updated_by IS 'Username of the user who last updated this invoice (e.g., payment recording)';

COMMENT ON CONSTRAINT chk_invoice_amounts ON invoices IS 'Ensures total >= paid and balance_due is correctly calculated';