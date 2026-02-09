package com.moeware.ims.entity;

import com.moeware.ims.entity.AppendOnlyEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Report entity for tracking generated reports and analytics
 *
 * Represents system-generated reports including:
 * - Stock valuation reports
 * - Inventory movement history
 * - Sales analysis
 * - Low stock alerts
 * - Purchase history
 *
 * Design Note: This is an append-only entity - reports are never modified after
 * generation.
 * Once a report is generated, it becomes an immutable record for compliance and
 * audit purposes.
 *
 * @author MoeWare Development Team
 * @since 1.0.0
 */
@Entity
@Table(name = "reports", indexes = {
        @Index(name = "idx_reports_generated_by", columnList = "generated_by"),
        @Index(name = "idx_reports_report_type", columnList = "report_type"),
        @Index(name = "idx_reports_status", columnList = "status"),
        @Index(name = "idx_reports_generated_by_at", columnList = "generated_by, generated_at DESC")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Report entity for tracking generated analytics and reports")
public class Report extends AppendOnlyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the report", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "report_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Type of report", example = "STOCK_VALUATION", allowableValues = { "STOCK_VALUATION",
            "MOVEMENT_HISTORY", "SALES_ANALYSIS", "LOW_STOCK", "PURCHASE_HISTORY" })
    private ReportType reportType;

    @Column(name = "name", nullable = false)
    @Schema(description = "Human-readable report name", example = "Monthly Stock Valuation - Jan 2026", maxLength = 255)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    @Schema(description = "Detailed description of what this report contains", example = "Comprehensive stock valuation report for all active warehouses, including cost values, retail values, and potential profit margins.")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generated_by", nullable = false, foreignKey = @ForeignKey(name = "fk_report_generated_by"))
    @Schema(description = "User who requested/generated this report", implementation = User.class)
    private User generatedBy;

    @Column(name = "parameters", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Schema(description = "Report generation parameters in JSON format", example = "{\"startDate\": \"2026-01-01\", \"endDate\": \"2026-01-31\", \"warehouseIds\": [1, 2], \"includeInactive\": false}", implementation = Map.class)
    private Map<String, Object> parameters;

    @Column(name = "file_url", length = 500)
    @Schema(description = "S3 or file system URL where the generated report file is stored", example = "https://s3.amazonaws.com/ims-reports/2026/01/stock-valuation-20260131-abc123.pdf")
    private String fileUrl;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Schema(description = "Current status of report generation", example = "COMPLETED", allowableValues = { "PENDING",
            "COMPLETED", "FAILED" }, defaultValue = "PENDING")
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "generated_at")
    @Schema(description = "Timestamp when the report generation was completed", example = "2026-01-31T16:45:30", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime generatedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    @Schema(description = "Error message if report generation failed", example = "Database connection timeout while fetching inventory data", accessMode = Schema.AccessMode.READ_ONLY)
    private String errorMessage;

    @Column(name = "file_size_bytes")
    @Schema(description = "Size of the generated report file in bytes", example = "2548736", accessMode = Schema.AccessMode.READ_ONLY)
    private Long fileSizeBytes;

    @Column(name = "row_count")
    @Schema(description = "Number of data rows/records included in the report", example = "1247", accessMode = Schema.AccessMode.READ_ONLY)
    private Integer rowCount;

    /**
     * Report type enumeration
     */
    @Schema(description = "Available report types in the system")
    public enum ReportType {
        @Schema(description = "Stock valuation report showing inventory value by warehouse")
        STOCK_VALUATION,

        @Schema(description = "Historical inventory movement tracking report")
        MOVEMENT_HISTORY,

        @Schema(description = "Sales performance and analysis report")
        SALES_ANALYSIS,

        @Schema(description = "Low stock alert report for reorder management")
        LOW_STOCK,

        @Schema(description = "Purchase order history and supplier performance report")
        PURCHASE_HISTORY
    }

    /**
     * Report generation status enumeration
     */
    @Schema(description = "Report generation status lifecycle")
    public enum ReportStatus {
        @Schema(description = "Report generation request is queued and waiting to be processed")
        PENDING,

        @Schema(description = "Report has been successfully generated and is available for download")
        COMPLETED,

        @Schema(description = "Report generation failed due to an error")
        FAILED
    }

    /**
     * Marks the report as completed and sets the generation timestamp
     *
     * @param fileUrl URL where the generated report is stored
     */
    public void markAsCompleted(String fileUrl) {
        this.status = ReportStatus.COMPLETED;
        this.fileUrl = fileUrl;
        this.generatedAt = LocalDateTime.now();
        this.errorMessage = null; // Clear any previous error
    }

    /**
     * Marks the report as completed with file metadata
     *
     * @param fileUrl       URL where the generated report is stored
     * @param fileSizeBytes Size of the generated file in bytes
     * @param rowCount      Number of data rows in the report
     */
    public void markAsCompleted(String fileUrl, Long fileSizeBytes, Integer rowCount) {
        markAsCompleted(fileUrl);
        this.fileSizeBytes = fileSizeBytes;
        this.rowCount = rowCount;
    }

    /**
     * Marks the report as failed and records the error message
     *
     * @param errorMessage Description of why the report generation failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = ReportStatus.FAILED;
        this.errorMessage = errorMessage;
        this.generatedAt = LocalDateTime.now();
    }

    /**
     * Checks if the report generation is still pending
     *
     * @return true if status is PENDING
     */
    public boolean isPending() {
        return this.status == ReportStatus.PENDING;
    }

    /**
     * Checks if the report generation completed successfully
     *
     * @return true if status is COMPLETED
     */
    public boolean isCompleted() {
        return this.status == ReportStatus.COMPLETED;
    }

    /**
     * Checks if the report generation failed
     *
     * @return true if status is FAILED
     */
    public boolean isFailed() {
        return this.status == ReportStatus.FAILED;
    }

    /**
     * Checks if the report file is available for download
     *
     * @return true if report is completed and has a valid file URL
     */
    public boolean isDownloadable() {
        return isCompleted() && fileUrl != null && !fileUrl.isEmpty();
    }

    /**
     * Gets a specific parameter from the report parameters JSON
     *
     * @param key The parameter key
     * @return The parameter value, or null if not found
     */
    public Object getParameter(String key) {
        return parameters != null ? parameters.get(key) : null;
    }

    /**
     * Gets a specific parameter as a String
     *
     * @param key The parameter key
     * @return The parameter value as String, or null if not found
     */
    public String getParameterAsString(String key) {
        Object value = getParameter(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Calculates the file size in a human-readable format
     *
     * @return Formatted file size (e.g., "2.43 MB")
     */
    public String getFormattedFileSize() {
        if (fileSizeBytes == null || fileSizeBytes == 0) {
            return "Unknown";
        }

        double size = fileSizeBytes;
        String[] units = { "B", "KB", "MB", "GB", "TB" };
        int unitIndex = 0;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.2f %s", size, units[unitIndex]);
    }

    /**
     * Gets the time taken to generate the report in seconds
     *
     * @return Duration in seconds, or null if not yet completed
     */
    public Long getGenerationDurationSeconds() {
        if (getCreatedAt() == null || generatedAt == null) {
            return null;
        }
        return java.time.Duration.between(getCreatedAt(), generatedAt).getSeconds();
    }

    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (status == null) {
            status = ReportStatus.PENDING;
        }
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", type=" + reportType +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", generatedBy=" + (generatedBy != null ? generatedBy.getId() : null) +
                ", createdAt=" + getCreatedAt() +
                ", generatedAt=" + generatedAt +
                '}';
    }
}