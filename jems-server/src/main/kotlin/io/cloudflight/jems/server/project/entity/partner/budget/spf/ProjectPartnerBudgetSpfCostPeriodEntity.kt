package io.cloudflight.jems.server.project.entity.partner.budget.spf

import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetPeriodBase
import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_spfcost_period")
class ProjectPartnerBudgetSpfCostPeriodEntity(
    @EmbeddedId
    override val budgetPeriodId: BudgetPeriodId<ProjectPartnerBudgetSpfCostEntity>,
    @field:NotNull
    override val amount: BigDecimal
) : ProjectPartnerBudgetPeriodBase<ProjectPartnerBudgetSpfCostEntity> {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetSpfCostPeriodEntity &&
            budgetPeriodId == other.budgetPeriodId

    override fun hashCode() =
        budgetPeriodId.hashCode()
}
