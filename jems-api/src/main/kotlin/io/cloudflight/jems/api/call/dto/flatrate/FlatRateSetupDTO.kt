package io.cloudflight.jems.api.call.dto.flatrate

data class FlatRateSetupDTO (
    val staffCostFlatRateSetup: FlatRateDTO? = null,
    val officeOnStaffFlatRateSetup: FlatRateDTO? = null,
    val officeOnOtherFlatRateSetup: FlatRateDTO? = null,
    val travelOnStaffFlatRateSetup: FlatRateDTO? = null,
    val otherOnStaffFlatRateSetup: FlatRateDTO? = null,
)
