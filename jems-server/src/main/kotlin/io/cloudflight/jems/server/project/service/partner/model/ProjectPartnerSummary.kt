package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO

data class ProjectPartnerSummary(
    val id: Long?,
    val abbreviation: String,
    val role: ProjectPartnerRoleDTO,
    val sortNumber: Int? = null,
    val country: String? = null
)
