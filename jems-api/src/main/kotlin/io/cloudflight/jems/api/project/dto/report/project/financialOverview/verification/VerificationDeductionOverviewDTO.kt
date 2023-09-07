package io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification

data class VerificationDeductionOverviewDTO(
    val deductionRows: List<VerificationDeductionOverviewRowDTO>,
    val staffCostsFlatRate: Int? = null,
    val officeAndAdministrationFlatRate: Int? = null,
    val travelAndAccommodationFlatRate: Int? = null,
    val otherCostsOnStaffCostsFlatRate: Int? = null,
    val total: VerificationDeductionOverviewRowDTO
)
