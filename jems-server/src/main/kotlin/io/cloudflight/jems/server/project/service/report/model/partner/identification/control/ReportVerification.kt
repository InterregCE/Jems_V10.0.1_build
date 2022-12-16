package io.cloudflight.jems.server.project.service.report.model.partner.identification.control

data class ReportVerification(
    val generalMethodologies: Set<ReportMethodology>,
    val verificationInstances: List<ReportOnTheSpotVerification>,
    val riskBasedVerificationApplied: Boolean,
    val riskBasedVerificationDescription: String?
)
