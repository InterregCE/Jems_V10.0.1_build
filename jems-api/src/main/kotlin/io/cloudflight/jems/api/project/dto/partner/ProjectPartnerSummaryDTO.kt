package io.cloudflight.jems.api.project.dto.partner

import java.math.BigDecimal

data class ProjectPartnerSummaryDTO (
    val id: Long?,
    val abbreviation: String,
    val role: ProjectPartnerRoleDTO,
    val sortNumber: Int? = null,
    val country: String? = null,
    val region: String? = null,
    val totalBudget: BigDecimal? = BigDecimal(0)
)
