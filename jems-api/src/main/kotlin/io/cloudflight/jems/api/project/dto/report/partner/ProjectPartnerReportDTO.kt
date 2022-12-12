package io.cloudflight.jems.api.project.dto.report.partner

data class ProjectPartnerReportDTO(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatusDTO,
    val linkedFormVersion: String,
    val identification: PartnerReportIdentificationDTO
)
