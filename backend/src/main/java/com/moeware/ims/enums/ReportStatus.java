package com.moeware.ims.enums;

import io.swagger.v3.oas.annotations.media.Schema;

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