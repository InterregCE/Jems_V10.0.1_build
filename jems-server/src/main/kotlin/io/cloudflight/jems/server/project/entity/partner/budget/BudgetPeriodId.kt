package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.entity.ProjectPeriodEntity
import java.io.Serializable
import javax.persistence.Embeddable
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.JoinColumns
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Embeddable
class BudgetPeriodId<T : ProjectPartnerBudgetBase>(

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_id")
    @field:NotNull
    val budget: T,


    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumns(
        JoinColumn(name = "project_id", referencedColumnName = "project_id"),
        JoinColumn(name = "period_number", referencedColumnName = "number")
    )
    @field:NotNull
    val period: ProjectPeriodEntity

) : Serializable {
    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is BudgetPeriodId<*> &&
            budget == other.budget &&
            period.id == other.period.id

    override fun hashCode() =
        budget.hashCode().plus(period.id.hashCode())


}
