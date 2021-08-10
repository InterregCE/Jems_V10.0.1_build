package io.cloudflight.jems.api.project.dto.partner

import io.cloudflight.jems.api.project.dto.ProjectContactType

data class ProjectPartnerContactDTO (
    val type: ProjectContactType,
    val title: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val telephone: String? = null
)
