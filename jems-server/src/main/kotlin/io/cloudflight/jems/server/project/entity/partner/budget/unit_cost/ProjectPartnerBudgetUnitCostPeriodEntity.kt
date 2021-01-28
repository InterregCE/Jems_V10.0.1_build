package io.cloudflight.jems.server.project.entity.partner.budget.unit_cost

import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetPeriodBase
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_unit_cost_period")
class ProjectPartnerBudgetUnitCostPeriodEntity(
    @EmbeddedId
    override val budgetPeriodId: BudgetPeriodId<ProjectPartnerBudgetUnitCostEntity>,
    @field:NotNull
    override val amount: BigDecimal

) : ProjectPartnerBudgetPeriodBase<ProjectPartnerBudgetUnitCostEntity> {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetUnitCostPeriodEntity &&
            budgetPeriodId == other.budgetPeriodId

    override fun hashCode() =
        if (budgetPeriodId.budget.id <= 0) super.hashCode()
        else budgetPeriodId.budget.id.toInt().plus(budgetPeriodId.period.id.hashCode())
}
