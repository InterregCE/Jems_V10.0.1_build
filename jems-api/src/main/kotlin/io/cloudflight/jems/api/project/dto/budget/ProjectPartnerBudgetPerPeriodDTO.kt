package io.cloudflight.jems.api.project.dto.budget

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import java.math.BigDecimal

data class ProjectPartnerBudgetPerPeriodDTO(
    val partner: ProjectPartnerSummaryDTO,
    val periodBudgets: Set<ProjectPeriodBudgetDTO> = emptySet(),
    val totalPartnerBudget: BigDecimal = BigDecimal.ZERO,
    val costType: ProjectPartnerCostTypeDTO
)
