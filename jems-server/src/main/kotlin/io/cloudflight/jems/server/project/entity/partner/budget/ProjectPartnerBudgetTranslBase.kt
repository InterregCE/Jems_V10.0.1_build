package io.cloudflight.jems.server.project.entity.partner.budget

interface ProjectPartnerBudgetTranslBase{
    val budgetTranslation: BudgetTranslation<out ProjectPartnerBudgetBase>
}
