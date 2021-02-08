package io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure

import io.cloudflight.jems.server.project.entity.partner.budget.BudgetPeriodId
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetPeriodBase
import java.math.BigDecimal
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_infrastructure_period")
class ProjectPartnerBudgetInfrastructurePeriodEntity(
    @EmbeddedId
    override val budgetPeriodId: BudgetPeriodId<ProjectPartnerBudgetInfrastructureEntity>,
    @field:NotNull
    override val amount: BigDecimal

) : ProjectPartnerBudgetPeriodBase<ProjectPartnerBudgetInfrastructureEntity> {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetInfrastructurePeriodEntity &&
            budgetPeriodId == other.budgetPeriodId

    override fun hashCode() =
        budgetPeriodId.hashCode()
}
