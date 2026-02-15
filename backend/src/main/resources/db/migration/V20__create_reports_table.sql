-- =========================================
-- V20: Create Reports Table
-- Created: 2026-02-15
-- Description: Generated reports and analytics tracking (append-only entity)
-- =========================================

-- Create REPORTS table
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    report_type VARCHAR(50) NOT NULL CHECK (report_type IN ('STOCK_VALUATION', 'MOVEMENT_HISTORY', 'SALES_ANALYSIS', 'LOW_STOCK', 'PURCHASE_HISTORY')),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    generated_by BIGINT NOT NULL,
    parameters JSONB,
    file_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED')),
    generated_at TIMESTAMP,
    error_message TEXT,
    file_size_bytes BIGINT,
    row_count INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),

    CONSTRAINT fk_report_generated_by FOREIGN KEY (generated_by)
        REFERENCES users(id) ON DELETE RESTRICT
);

-- Indexes
CREATE INDEX idx_reports_generated_by ON reports(generated_by);

CREATE INDEX idx_reports_report_type ON reports(report_type);

CREATE INDEX idx_reports_status ON reports(status);

CREATE INDEX idx_reports_generated_by_at ON reports(generated_by, generated_at DESC);

-- Comments
COMMENT ON TABLE reports IS 'Generated reports and analytics tracking (append-only entity - reports are immutable once generated)';

COMMENT ON COLUMN reports.report_type IS 'Type of report: STOCK_VALUATION, MOVEMENT_HISTORY, SALES_ANALYSIS, LOW_STOCK, PURCHASE_HISTORY';

COMMENT ON COLUMN reports.name IS 'Human-readable report name for identification';

COMMENT ON COLUMN reports.description IS 'Detailed description of report contents and purpose';

COMMENT ON COLUMN reports.generated_by IS 'User who requested/generated this report';

COMMENT ON COLUMN reports.parameters IS 'Report generation parameters stored as JSONB (e.g., date ranges, warehouse filters)';

COMMENT ON COLUMN reports.file_url IS 'S3 or file system URL where the generated report file is stored';

COMMENT ON COLUMN reports.status IS 'Generation status: PENDING, COMPLETED, FAILED';

COMMENT ON COLUMN reports.generated_at IS 'Timestamp when report generation completed successfully';

COMMENT ON COLUMN reports.error_message IS 'Error description if report generation failed';

COMMENT ON COLUMN reports.file_size_bytes IS 'Size of the generated report file in bytes';

COMMENT ON COLUMN reports.row_count IS 'Number of data rows/records included in the report';

COMMENT ON COLUMN reports.created_at IS 'Report request timestamp (immutable)';

COMMENT ON COLUMN reports.created_by IS 'User who initiated the report generation request';

COMMENT ON CONSTRAINT fk_report_generated_by ON reports IS 'RESTRICT delete: Cannot delete users who have generated reports (data integrity)';