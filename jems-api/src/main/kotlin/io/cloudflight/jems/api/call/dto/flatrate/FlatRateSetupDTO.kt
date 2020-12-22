package io.cloudflight.jems.api.call.dto.flatrate

data class FlatRateSetupDTO (
    val staffCostFlatRateSetup: FlatRateDTO? = null,
    val officeAndAdministrationOnStaffCostsFlatRate: FlatRateDTO? = null,
    val officeAndAdministrationOnOtherCostsFlatRateSetup: FlatRateDTO? = null,
    val travelAndAccommodationOnStaffCostsFlatRateSetup: FlatRateDTO? = null,
    val otherCostsOnStaffCostsFlatRateSetup: FlatRateDTO? = null,
)
