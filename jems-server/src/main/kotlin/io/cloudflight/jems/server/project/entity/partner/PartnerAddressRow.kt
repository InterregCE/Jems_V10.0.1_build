package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerAddressTypeDTO

interface PartnerAddressRow {
    val type: ProjectPartnerAddressTypeDTO
    val country: String?
    val nutsRegion2: String?
    val nutsRegion3: String?
    val street: String?
    val houseNumber: String?
    val postalCode: String?
    val city: String?
    val homepage: String?
}
