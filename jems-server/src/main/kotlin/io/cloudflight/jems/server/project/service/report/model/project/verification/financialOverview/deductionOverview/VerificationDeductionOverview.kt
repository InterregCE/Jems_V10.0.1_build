package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview

data class VerificationDeductionOverview(
    val deductionRows: List<VerificationDeductionOverviewRow>,
    val staffCostsFlatRate: Int? = null,
    val officeAndAdministrationFlatRate: Int? = null,
    val travelAndAccommodationFlatRate: Int? = null,
    val otherCostsOnStaffCostsFlatRate: Int? = null,
    val total: VerificationDeductionOverviewRow
)