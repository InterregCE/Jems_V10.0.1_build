package io.cloudflight.jems.server.project.service.partner.model

import io.cloudflight.jems.server.project.service.model.ProjectContactType

data class ProjectPartnerContact(
    val type: ProjectContactType,
    val title: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val telephone: String? = null
)
