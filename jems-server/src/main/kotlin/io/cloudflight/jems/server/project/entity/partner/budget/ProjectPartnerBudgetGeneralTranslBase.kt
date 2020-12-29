package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.entity.BudgetTranslation

interface ProjectPartnerBudgetGeneralTranslBase {
    val budgetTranslation: BudgetTranslation<out ProjectPartnerBudgetGeneralBase>
    val awardProcedures: String?
    val unitType: String?
    val description: String?
}
