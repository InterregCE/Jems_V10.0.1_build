package io.cloudflight.jems.api.project.dto.partner

data class OutputProjectPartner (
    val id: Long?,
    val abbreviation: String,
    val role: ProjectPartnerRole,
    val sortNumber: Int? = null,
    val country: String? = null
)
