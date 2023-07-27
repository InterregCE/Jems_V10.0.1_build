package io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure

data class ProjectReportVerificationRiskBased(
    val projectReportId: Long,
    val riskBasedVerification: Boolean,
    val riskBasedVerificationDescription: String?
)
