package io.cloudflight.jems.server.project.service.partner.model

import java.math.BigDecimal

data class BudgetUnitCostEntry(
    val id: Long? = null,
    val numberOfUnits: BigDecimal,

    val unitCostId: Long,

    val rowSum: BigDecimal
)
