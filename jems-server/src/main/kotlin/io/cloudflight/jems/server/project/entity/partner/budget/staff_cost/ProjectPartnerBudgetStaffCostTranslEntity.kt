package io.cloudflight.jems.server.project.entity.partner.budget.staff_cost

import io.cloudflight.jems.server.project.entity.partner.budget.BudgetTranslation
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTranslBase
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_staff_cost_transl")
data class ProjectPartnerBudgetStaffCostTranslEntity(

    @EmbeddedId
    override val budgetTranslation: BudgetTranslation<ProjectPartnerBudgetStaffCostEntity>,

    val description: String? = null,

    val comment: String? = null

) : ProjectPartnerBudgetTranslBase {
    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetStaffCostTranslEntity &&
            budgetTranslation == other.budgetTranslation

    override fun hashCode() =
        budgetTranslation.hashCode()
}
