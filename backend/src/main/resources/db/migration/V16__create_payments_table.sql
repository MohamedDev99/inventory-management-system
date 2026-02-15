-- =========================================
-- V16: Create Payments Table
-- Created: 2026-01-28
-- Updated: 2026-02-14 (Added version field for VersionedEntity)
-- Description: Payment transactions with status tracking and optimistic locking
-- =========================================

-- Create PAYMENTS table
CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    payment_number VARCHAR(50) NOT NULL UNIQUE,
    sales_order_id BIGINT,
    customer_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL CHECK (payment_method IN ('CASH', 'CREDIT_CARD', 'DEBIT_CARD', 'BANK_TRANSFER', 'CHECK', 'PAYPAL')),
    amount DECIMAL(12,2) NOT NULL CHECK (amount > 0),
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    reference_number VARCHAR(100),
    payment_status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (payment_status IN ('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED')),
    notes TEXT,
    processed_by BIGINT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),

    CONSTRAINT fk_payment_sales_order FOREIGN KEY (sales_order_id)
        REFERENCES sales_orders(id) ON DELETE SET NULL,
    CONSTRAINT fk_payment_customer FOREIGN KEY (customer_id)
        REFERENCES customers(id) ON DELETE RESTRICT,
    CONSTRAINT fk_payment_processed_by FOREIGN KEY (processed_by)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_payment_sales_order ON payments(sales_order_id);

CREATE INDEX idx_payment_customer ON payments(customer_id);

CREATE INDEX idx_payment_status ON payments(payment_status);

CREATE INDEX idx_payment_date ON payments(payment_date DESC);

CREATE INDEX idx_payment_method ON payments(payment_method);

CREATE INDEX idx_payment_reference ON payments(reference_number) WHERE reference_number IS NOT NULL;

CREATE INDEX idx_payment_processed_by ON payments(processed_by);

CREATE INDEX idx_payment_customer_date ON payments(customer_id, payment_date DESC);

-- Create trigger to automatically update updated_at
CREATE TRIGGER update_payments_updated_at
    BEFORE UPDATE ON payments
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- Comments
COMMENT ON TABLE payments IS 'Payment transaction records for customer orders';

COMMENT ON COLUMN payments.payment_number IS 'Unique payment transaction number (format: PAY-YYYYMMDD-XXXX)';

COMMENT ON COLUMN payments.sales_order_id IS 'Related sales order (NULL for advance payments or account credits)';

COMMENT ON COLUMN payments.payment_method IS 'Payment method: CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, CHECK, PAYPAL';

COMMENT ON COLUMN payments.payment_status IS 'Transaction status: PENDING, COMPLETED, FAILED, REFUNDED';

COMMENT ON COLUMN payments.reference_number IS 'External reference (check number, transaction ID, etc.)';

COMMENT ON COLUMN payments.currency IS 'ISO 4217 currency code (default: USD)';

COMMENT ON COLUMN payments.version IS 'Optimistic lock version - CRITICAL for financial transaction integrity';

COMMENT ON COLUMN payments.created_by IS 'Username of the user who created this payment record';

COMMENT ON COLUMN payments.updated_by IS 'Username of the user who last updated this payment (e.g., refund processing)';