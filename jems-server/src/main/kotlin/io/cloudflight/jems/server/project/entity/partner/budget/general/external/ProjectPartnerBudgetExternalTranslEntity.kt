package io.cloudflight.jems.server.project.entity.partner.budget.general.external

import io.cloudflight.jems.server.project.entity.partner.budget.BudgetTranslation
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralTranslBase
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_external_transl")
data class ProjectPartnerBudgetExternalTranslEntity(

    @EmbeddedId
    override val budgetTranslation: BudgetTranslation<ProjectPartnerBudgetExternalEntity>,

    override val awardProcedures: String? = null,

    override val unitType: String? = null,

    override val description: String? = null

) : ProjectPartnerBudgetGeneralTranslBase {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetExternalTranslEntity &&
            budgetTranslation == other.budgetTranslation

    override fun hashCode() =
        budgetTranslation.hashCode()
}
