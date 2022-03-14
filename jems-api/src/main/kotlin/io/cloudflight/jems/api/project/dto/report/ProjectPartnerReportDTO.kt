package io.cloudflight.jems.api.project.dto.report

import io.cloudflight.jems.api.project.dto.report.partner.PartnerReportIdentificationDTO

data class ProjectPartnerReportDTO(
    val id: Long,
    val reportNumber: Int,
    val status: ReportStatusDTO,
    val linkedFormVersion: String,
    val identification: PartnerReportIdentificationDTO
)
