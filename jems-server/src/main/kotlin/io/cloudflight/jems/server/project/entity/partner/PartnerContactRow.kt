package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.server.project.service.model.ProjectContactType

interface PartnerContactRow {
    val type: ProjectContactType
    val title: String?
    val firstName: String?
    val lastName: String?
    val email: String?
    val telephone: String?
}
