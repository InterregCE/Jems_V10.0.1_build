package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.entity.BudgetTranslation
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_equipment_transl")
class ProjectPartnerBudgetEquipmentTransl(

    @EmbeddedId
    override val budgetTranslation: BudgetTranslation<ProjectPartnerBudgetEquipmentEntity>,

    override val awardProcedures: String? = null,

    override val unitType: String? = null,

    override val description: String? = null

) : ProjectPartnerBudgetGeneralTranslBase {

    override fun equals(other: Any?) =
        if (this === other) true
        else
            other !== null &&
                other is ProjectPartnerBudgetStaffCostTransl &&
                budgetTranslation == other.budgetTranslation

    override fun hashCode() =
        if (budgetTranslation.budget.id <= 0) super.hashCode()
        else budgetTranslation.budget.id.toInt().plus(budgetTranslation.language.translationKey.hashCode())
}
