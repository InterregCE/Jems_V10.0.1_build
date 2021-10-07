package io.cloudflight.jems.server.project.service.partner.model

import java.math.BigDecimal

data class ProjectPartnerSummary(
    val id: Long?,
    val abbreviation: String,
    val role: ProjectPartnerRole,
    val sortNumber: Int? = null,
    val country: String? = null,
    val region: String? = null,
)
