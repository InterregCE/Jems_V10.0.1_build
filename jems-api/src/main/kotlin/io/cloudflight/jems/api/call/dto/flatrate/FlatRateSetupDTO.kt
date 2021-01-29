package io.cloudflight.jems.api.call.dto.flatrate

data class FlatRateSetupDTO (
    val staffCostFlatRateSetup: FlatRateDTO? = null,
    val officeAndAdministrationOnStaffCostsFlatRateSetup: FlatRateDTO? = null,
    val officeAndAdministrationOnDirectCostsFlatRateSetup: FlatRateDTO? = null,
    val travelAndAccommodationOnStaffCostsFlatRateSetup: FlatRateDTO? = null,
    val otherCostsOnStaffCostsFlatRateSetup: FlatRateDTO? = null
)
