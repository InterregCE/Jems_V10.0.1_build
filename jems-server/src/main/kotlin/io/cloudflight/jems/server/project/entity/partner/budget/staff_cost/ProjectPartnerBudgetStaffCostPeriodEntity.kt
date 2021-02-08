package io.cloudflight.jems.server.project.entity.partner.budget.staff_cost

import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetPeriodBase
import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_staff_cost_period")
class ProjectPartnerBudgetStaffCostPeriodEntity(
    @EmbeddedId
    override val budgetPeriodId: BudgetPeriodId<ProjectPartnerBudgetStaffCostEntity>,
    @field:NotNull
    override val amount: BigDecimal

) : ProjectPartnerBudgetPeriodBase<ProjectPartnerBudgetStaffCostEntity> {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetStaffCostPeriodEntity &&
            budgetPeriodId == other.budgetPeriodId

    override fun hashCode() =
        budgetPeriodId.hashCode()
}
