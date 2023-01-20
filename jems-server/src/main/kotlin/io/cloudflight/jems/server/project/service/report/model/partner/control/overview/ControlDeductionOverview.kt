package io.cloudflight.jems.server.project.service.report.model.partner.control.overview

data class ControlDeductionOverview (
    val deductionRows: List<ControlDeductionOverviewRow>,
    val staffCostsFlatRate: Int? = null,
    val officeAndAdministrationFlatRate: Int? = null,
    val travelAndAccommodationFlatRate: Int? = null,
    val otherCostsOnStaffCostsFlatRate: Int? = null,
    val total: ControlDeductionOverviewRow
)
