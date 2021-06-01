package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressType

interface PartnerAddressRow {
    val type: ProjectPartnerAddressType
    val country: String?
    val nutsRegion2: String?
    val nutsRegion3: String?
    val street: String?
    val houseNumber: String?
    val postalCode: String?
    val city: String?
    val homepage: String?
}
