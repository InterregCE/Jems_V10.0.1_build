package io.cloudflight.jems.server.project.service.partner

fun getPartnerAddressOrEmptyString(
    country: String?,
    city: String?,
    postalCode: String?,
    street: String? = "",
    houseNumber: String? = ""
): String =
    listOf(country, city, postalCode).any { it.isNullOrEmpty() }.let {
        if (it) {
            ""
        } else {
            "$country, $city, $postalCode, ${street.orEmpty()}, ${houseNumber.orEmpty()}".replace(", ,", "").trimEnd()
        }
    }


