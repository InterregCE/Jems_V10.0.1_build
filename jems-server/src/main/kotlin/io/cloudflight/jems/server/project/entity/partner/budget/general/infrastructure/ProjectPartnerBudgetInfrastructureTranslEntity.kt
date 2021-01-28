package io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure

import io.cloudflight.jems.server.project.entity.BudgetTranslation
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralTranslBase
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_infrastructure_transl")
data class ProjectPartnerBudgetInfrastructureTranslEntity(

    @EmbeddedId
    override val budgetTranslation: BudgetTranslation<ProjectPartnerBudgetInfrastructureEntity>,

    override val awardProcedures: String? = null,

    override val unitType: String? = null,

    override val description: String? = null

) : ProjectPartnerBudgetGeneralTranslBase {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetInfrastructureTranslEntity &&
            budgetTranslation == other.budgetTranslation

    override fun hashCode() =
        if (budgetTranslation.budget.id <= 0) super.hashCode()
        else budgetTranslation.budget.id.toInt().plus(budgetTranslation.language.translationKey.hashCode())
}
