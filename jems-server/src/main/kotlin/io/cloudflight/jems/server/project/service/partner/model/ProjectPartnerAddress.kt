package io.cloudflight.jems.server.project.service.partner.model

data class ProjectPartnerAddress(
    val type: ProjectPartnerAddressType,
    val country: String? = null,
    val countryCode: String? = null,
    val nutsRegion2: String? = null,
    val nutsRegion2Code: String? = null,
    val nutsRegion3: String? = null,
    val nutsRegion3Code: String? = null,
    val street: String? = null,
    val houseNumber: String? = null,
    val postalCode: String? = null,
    val city: String? = null,
    val homepage: String? = null
)
