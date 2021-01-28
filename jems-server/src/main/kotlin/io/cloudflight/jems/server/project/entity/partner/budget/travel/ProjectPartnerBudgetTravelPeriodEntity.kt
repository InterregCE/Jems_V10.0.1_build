package io.cloudflight.jems.server.project.entity.partner.budget.travel

import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetPeriodBase
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_travel_period")
class ProjectPartnerBudgetTravelPeriodEntity(
    @EmbeddedId
    override val budgetPeriodId: BudgetPeriodId<ProjectPartnerBudgetTravelEntity>,
    @field:NotNull
    override val amount: BigDecimal
) : ProjectPartnerBudgetPeriodBase<ProjectPartnerBudgetTravelEntity> {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetTravelPeriodEntity &&
            budgetPeriodId == other.budgetPeriodId

    override fun hashCode() =
        if (budgetPeriodId.budget.id <= 0) super.hashCode()
        else budgetPeriodId.budget.id.toInt().plus(budgetPeriodId.period.id.hashCode())
}
