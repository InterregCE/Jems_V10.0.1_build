package io.cloudflight.jems.api.project.dto.partner

data class ProjectPartnerDTO (
    val id: Long?,
    val abbreviation: String,
    val role: ProjectPartnerRoleDTO,
    val sortNumber: Int? = null,
    val country: String? = null
)
