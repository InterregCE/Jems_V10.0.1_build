package io.cloudflight.jems.api.project.dto.budget

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.math.BigDecimal

data class ProjectFundBudgetPerPeriodDTO(
    val fund: ProgrammeFundDTO,
    val costType: ProjectPartnerCostTypeDTO,
    val periodFunds: Set<ProjectPeriodFundDTO> = emptySet(),
    val totalFundBudget: BigDecimal = BigDecimal.ZERO
)
