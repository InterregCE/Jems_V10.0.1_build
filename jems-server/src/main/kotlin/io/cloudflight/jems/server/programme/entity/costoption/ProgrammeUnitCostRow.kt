package io.cloudflight.jems.server.programme.entity.costoption

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import java.math.BigDecimal

interface ProgrammeUnitCostRow {
    val id: Long
    val projectId: Long?
    val oneCostCategory: Boolean
    val costPerUnit: BigDecimal
    val costPerUnitForeignCurrency: BigDecimal?
    val foreignCurrencyCode: String?
    val language: SystemLanguage
    val name: String?
    val description: String?
    val type: String?
    val justification: String?
    val category: BudgetCategory
}
