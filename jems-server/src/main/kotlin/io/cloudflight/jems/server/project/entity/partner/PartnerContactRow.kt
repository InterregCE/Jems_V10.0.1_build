package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.project.dto.ProjectContactTypeDTO

interface PartnerContactRow {
    val type: ProjectContactTypeDTO
    val title: String?
    val firstName: String?
    val lastName: String?
    val email: String?
    val telephone: String?
}
