package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.programme.entity.costoption.ProgrammeUnitCostEntity
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_unit_cost")
data class ProjectPartnerBudgetUnitCostEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programme_unit_cost_id")
    val unitCost: ProgrammeUnitCostEntity?,

    @field:NotNull
    val partnerId: Long,

    @field:NotNull
    val numberOfUnits: BigDecimal,

    @field:NotNull
    val rowSum: BigDecimal

) {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetUnitCostEntity &&
            id > 0 &&
            id == other.id

    override fun hashCode() =
        if (id > 0) id.toInt() else super.hashCode()

}
