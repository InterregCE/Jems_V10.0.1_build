package io.cloudflight.jems.api.project.dto.associatedorganization

import io.cloudflight.jems.api.project.dto.InputAddress

data class InputProjectAssociatedOrganizationAddress (

    override val country: String? = null,
    override val countryCode: String? = null,
    override val nutsRegion2: String? = null,
    override val nutsRegion2Code: String? = null,
    override val nutsRegion3: String? = null,
    override val nutsRegion3Code: String? = null,
    override val street: String? = null,
    override val houseNumber: String? = null,
    override val postalCode: String? = null,
    override val city: String? = null,

    val homepage: String? = null

): InputAddress
