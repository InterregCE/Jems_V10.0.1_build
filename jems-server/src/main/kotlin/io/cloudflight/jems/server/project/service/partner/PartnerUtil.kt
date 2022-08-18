package io.cloudflight.jems.server.project.service.partner

fun getPartnerAddressOrEmptyString(country: String?, city: String?, street: String?, houseNumber: String?): String =
    listOf(country, city, street, houseNumber).any { it.isNullOrEmpty() }.let {
        if (it) {
            ""
        } else {
            "$city, $street, $houseNumber, $country"
        }
    }


