package io.cloudflight.jems.server.project.entity.partner.budget.general

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetBase
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetPeriodBase
import java.math.BigDecimal

interface ProjectPartnerBudgetGeneralBase :
    ProjectPartnerBudgetBase {
    val unitCostId: Long?
    val investmentId: Long?
    val pricePerUnit: BigDecimal
    val translatedValues: MutableSet<out ProjectPartnerBudgetGeneralTranslBase>
    val budgetPeriodEntities: MutableSet<out ProjectPartnerBudgetPeriodBase<out ProjectPartnerBudgetBase>>

}
