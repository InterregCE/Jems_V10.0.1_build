package io.cloudflight.jems.server.project.entity.partner.budget.general.equipment

import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetPeriodBase
import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_equipment_period")
class ProjectPartnerBudgetEquipmentPeriodEntity(
    @EmbeddedId
    override val budgetPeriodId: BudgetPeriodId<ProjectPartnerBudgetEquipmentEntity>,
    @field:NotNull
    override val amount: BigDecimal

) : ProjectPartnerBudgetPeriodBase<ProjectPartnerBudgetEquipmentEntity> {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetEquipmentPeriodEntity &&
            budgetPeriodId == other.budgetPeriodId

    override fun hashCode() =
        if (budgetPeriodId.budget.id <= 0) super.hashCode()
        else budgetPeriodId.budget.id.toInt().plus(budgetPeriodId.period.id.hashCode())
}
