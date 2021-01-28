package io.cloudflight.jems.api.project.dto.partner.budget

import io.swagger.annotations.ApiModel
import java.math.BigDecimal

@ApiModel(value = "BudgetPeriodDTO")
data class BudgetPeriodDTO(
    val number: Int,
    val amount: BigDecimal? = BigDecimal.ZERO
)
