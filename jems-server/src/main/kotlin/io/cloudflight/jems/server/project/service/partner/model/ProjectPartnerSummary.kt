package io.cloudflight.jems.server.project.service.partner.model

data class ProjectPartnerSummary(
    val id: Long?,
    val abbreviation: String,
    val role: ProjectPartnerRole,
    val sortNumber: Int? = null,
    val country: String? = null,
    val region: String? = null,
)
