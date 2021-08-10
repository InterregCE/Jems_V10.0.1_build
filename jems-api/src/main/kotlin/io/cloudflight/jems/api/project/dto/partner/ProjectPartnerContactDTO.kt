package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.ProjectContactTypeDTO

data class ProjectPartnerContactDTO (
    val type: ProjectContactTypeDTO,
    val title: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val telephone: String? = null
)
