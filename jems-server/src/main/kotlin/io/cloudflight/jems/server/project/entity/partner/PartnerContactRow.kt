package io.cloudflight.jems.server.project.entity.partner

import io.cloudflight.jems.api.project.dto.ProjectContactType

interface PartnerContactRow {
    val partnerId: Long
    val type: ProjectContactType
    val title: String?
    val firstName: String?
    val lastName: String?
    val email: String?
    val telephone: String?
}