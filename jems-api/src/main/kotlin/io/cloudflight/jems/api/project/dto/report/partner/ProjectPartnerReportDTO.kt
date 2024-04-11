package io.cloudflight.jems.api.project.dto.report.partner

import java.time.ZonedDateTime

data class ProjectPartnerReportDTO(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatusDTO,
    val linkedFormVersion: String,
    val identification: PartnerReportIdentificationDTO,
    val lastControlReopening: ZonedDateTime?,
    val projectReportId: Long?,
    val projectReportNumber: Int?,
)
