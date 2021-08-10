package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO

interface PartnerSimpleRow {
    val id: Long?
    val abbreviation: String
    val role: ProjectPartnerRoleDTO
    val sortNumber: Int?
    val country: String?
}
