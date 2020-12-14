package io.cloudflight.jems.api.common.dto

data class AddressDTO(
    val country: String?,
    val nutsRegion2: String?,
    val nutsRegion3: String?,
    val street: String?,
    val houseNumber: String?,
    val postalCode: String?,
    val city: String?
)
