package io.cloudflight.jems.api.common.dto

data class AddressDTO(
    val country: String?,
    val region2: String?,
    val region3: String?,
    val street: String?,
    val houseNumber: String?,
    val postalCode: String?,
    val city: String?
)
