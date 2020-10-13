package io.cloudflight.jems.api.project.dto.partner

data class OutputProjectPartnerContact (
    val type: ProjectPartnerContactType,
    val title: String?,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val telephone: String?
)
