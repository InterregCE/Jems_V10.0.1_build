package io.cloudflight.jems.server.project.entity.associatedorganization

import io.cloudflight.jems.api.project.dto.ProjectContactType

interface AssociatedOrganizationContactRow {
    val type: ProjectContactType
    val title: String?
    val firstName: String?
    val lastName: String?
    val email: String?
    val telephone: String?
}