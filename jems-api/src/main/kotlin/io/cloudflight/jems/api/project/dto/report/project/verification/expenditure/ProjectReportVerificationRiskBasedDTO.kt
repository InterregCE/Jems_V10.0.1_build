package io.cloudflight.jems.api.project.dto.report.project.verification.expenditure

data class ProjectReportVerificationRiskBasedDTO(
    val projectReportId: Long,
    val riskBasedVerification: Boolean,
    val riskBasedVerificationDescription: String?
)
