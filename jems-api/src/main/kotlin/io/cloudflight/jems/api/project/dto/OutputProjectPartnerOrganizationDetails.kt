package io.cloudflight.jems.api.project.dto

data class OutputProjectPartnerOrganizationDetails(
    val type: OrganizationDetailType,
    val country: String?,
    val nutsRegion2: String?,
    val nutsRegion3: String?,
    val street: String?,
    val houseNumber: String?,
    val postalCode: String?,
    val city: String?,
    val homepage: String?
)

