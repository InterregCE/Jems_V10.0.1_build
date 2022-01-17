package io.cloudflight.jems.api.project.dto.budget

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import java.math.BigDecimal

data class PartnerBudgetPerFundDTO(
    val fund: ProgrammeFundDTO?,
    val percentage: BigDecimal,
    val percentageOfTotal: BigDecimal?,
    val value: BigDecimal,
)
