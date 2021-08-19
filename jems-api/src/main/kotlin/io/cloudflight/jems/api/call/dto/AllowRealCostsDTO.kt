package io.cloudflight.jems.api.call.dto

data class AllowRealCostsDTO(
    val allowRealStaffCosts: Boolean,
    val allowRealTravelAndAccommodationCosts: Boolean,
    var allowRealExternalExpertiseAndServicesCosts: Boolean,
    var allowRealEquipmentCosts: Boolean,
    var allowRealInfrastructureCosts: Boolean,
)
