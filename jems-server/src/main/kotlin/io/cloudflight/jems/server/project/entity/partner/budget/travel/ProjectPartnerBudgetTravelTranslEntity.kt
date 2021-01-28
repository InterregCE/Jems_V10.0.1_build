package io.cloudflight.jems.server.project.entity.partner.budget.travel

import io.cloudflight.jems.server.project.entity.BudgetTranslation
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTranslBase
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_travel_transl")
data class ProjectPartnerBudgetTravelTranslEntity(

    @EmbeddedId
    override val budgetTranslation: BudgetTranslation<ProjectPartnerBudgetTravelEntity>,

    val unitType: String? = null,

    val description: String? = null

): ProjectPartnerBudgetTranslBase {
    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetTravelTranslEntity &&
            budgetTranslation == other.budgetTranslation

    override fun hashCode() =
        if (budgetTranslation.budget.id <= 0) super.hashCode()
        else budgetTranslation.budget.id.toInt().plus(budgetTranslation.language.translationKey.hashCode())

}
