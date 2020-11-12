package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRole

data class ProjectPartner(
    val id: Long?,
    val abbreviation: String,
    val role: ProjectPartnerRole,
    val sortNumber: Int? = null,
    val country: String? = null
)
