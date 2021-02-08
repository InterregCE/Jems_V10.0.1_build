package io.cloudflight.jems.server.project.entity.partner.budget.unit_cost

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetBase
import javax.persistence.CascadeType
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_unit_cost")
data class ProjectPartnerBudgetUnitCostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @Embedded
    @field:NotNull
    override val baseProperties: BaseBudgetProperties,

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_unit_cost_id")
    @field:NotNull
    val unitCost: ProgrammeUnitCostEntity,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "budgetPeriodId.budget")
    val budgetPeriodEntities: MutableSet<ProjectPartnerBudgetUnitCostPeriodEntity>

) : ProjectPartnerBudgetBase {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetUnitCostEntity &&
            id > 0 &&
            id == other.id

    override fun hashCode() =
        super.hashCode()

}
