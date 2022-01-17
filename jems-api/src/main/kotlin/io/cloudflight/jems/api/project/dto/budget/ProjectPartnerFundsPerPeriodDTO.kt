package io.cloudflight.jems.api.project.dto.budget

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.math.BigDecimal

data class ProjectPartnerFundsPerPeriodDTO(
    val fund: ProgrammeFundDTO,
    val periodFunds: Set<ProjectPeriodFundDTO> = emptySet(),
    val totalFundBudget: BigDecimal = BigDecimal.ZERO
)
