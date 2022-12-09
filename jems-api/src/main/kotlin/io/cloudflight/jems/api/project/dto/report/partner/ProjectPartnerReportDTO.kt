package io.cloudflight.jems.api.project.dto.report.partner

import io.cloudflight.jems.api.project.dto.report.ReportStatusDTO

data class ProjectPartnerReportDTO(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatusDTO,
    val linkedFormVersion: String,
    val identification: PartnerReportIdentificationDTO
)
