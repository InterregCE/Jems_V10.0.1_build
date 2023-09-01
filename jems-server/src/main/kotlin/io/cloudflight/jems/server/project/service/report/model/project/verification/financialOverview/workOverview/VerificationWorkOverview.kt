package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview

data class VerificationWorkOverview(
    val certificates: List<VerificationWorkOverviewLine>,
    val total: VerificationWorkOverviewLine,
)
