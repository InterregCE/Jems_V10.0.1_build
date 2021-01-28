package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.entity.BudgetTranslation

interface ProjectPartnerBudgetTranslBase{
    val budgetTranslation: BudgetTranslation<out ProjectPartnerBudgetBase>
}
