package io.cloudflight.jems.api.project.dto.report.partner.control.overview


data class ControlDeductionOverviewDTO(
    val deductionRows: List<ControlDeductionOverviewRowDTO>,
    val staffCostsFlatRate: Int? = null,
    val officeAndAdministrationFlatRate: Int? = null,
    val travelAndAccommodationFlatRate: Int? = null,
    val otherCostsOnStaffCostsFlatRate: Int? = null,
    val total: ControlDeductionOverviewRowDTO
)
