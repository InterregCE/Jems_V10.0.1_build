package io.cloudflight.jems.api.project.dto.report.partner.expenditure

data class ProjectPartnerReportParkedLinkedDTO(
    val reportRelatedId: Long,
    val projectRelatedId: Long,
    val entityStillAvailable: Boolean,
)
