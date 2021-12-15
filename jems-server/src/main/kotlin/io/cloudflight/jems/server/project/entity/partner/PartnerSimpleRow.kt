package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole

interface PartnerSimpleRow {
    val id: Long?
    val abbreviation: String
    val role: ProjectPartnerRole
    val sortNumber: Int?
    val country: String?
    val nutsRegion2: String?
}
