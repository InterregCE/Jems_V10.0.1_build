package io.cloudflight.jems.api.project.dto

import javax.validation.constraints.NotNull

data class InputProjectPartnerOrganizationDetails(
    @field:NotNull(message = "project.partner.type.should.not.be.empty")
    val type: OrganizationDetailType,
    val country: String? = null,
    val nutsRegion2: String? = null,
    val nutsRegion3: String? = null,
    val street: String? = null,
    val houseNumber: String? = null,
    val postalCode: String? = null,
    val city: String? = null,
    val homepage: String? = null
)

