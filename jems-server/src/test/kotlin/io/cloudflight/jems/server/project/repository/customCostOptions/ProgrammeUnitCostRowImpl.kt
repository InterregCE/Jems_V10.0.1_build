package io.cloudflight.jems.server.project.repository.customCostOptions

import io.cloudflight.jems.api.programme.dto.costoption.BudgetCategory
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostRow
import java.math.BigDecimal

data class ProgrammeUnitCostRowImpl(
    override val id: Long,
    override val projectId: Long?,
    override val oneCostCategory: Boolean,
    override val costPerUnit: BigDecimal,
    override val costPerUnitForeignCurrency: BigDecimal?,
    override val foreignCurrencyCode: String?,
    override val language: SystemLanguage,
    override val name: String?,
    override val description: String?,
    override val type: String?,
    override val justification: String?,
    override val category: BudgetCategory
) : ProgrammeUnitCostRow
