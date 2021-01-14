package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.entity.BudgetTranslation
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * project partner budget travel lang table
 */
@Entity(name = "project_partner_budget_travel_transl")
data class ProjectPartnerBudgetTravelTransl(

    @EmbeddedId
    val budgetTranslation: BudgetTranslation<ProjectPartnerBudgetTravelEntity>,

    val unitType: String? = null,

    val description: String? = null

) {
    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetTravelTransl &&
            budgetTranslation == other.budgetTranslation

    override fun hashCode() =
        if (budgetTranslation.budget.id <= 0) super.hashCode()
        else budgetTranslation.budget.id.toInt().plus(budgetTranslation.language.translationKey.hashCode())

}
