package io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification

data class VerificationWorkOverviewDTO(
    val certificates: List<VerificationWorkOverviewLineDTO>,
    val total: VerificationWorkOverviewLineDTO,
)
