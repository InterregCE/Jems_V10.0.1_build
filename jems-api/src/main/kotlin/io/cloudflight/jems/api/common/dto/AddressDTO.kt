package io.cloudflight.jems.api.common.dto

data class AddressDTO(
    val country: String?,
    val countryCode: String?,
    val region2: String?,
    val region2Code: String?,
    val region3: String?,
    val region3Code: String?,
    val street: String?,
    val houseNumber: String?,
    val postalCode: String?,
    val city: String?
)
