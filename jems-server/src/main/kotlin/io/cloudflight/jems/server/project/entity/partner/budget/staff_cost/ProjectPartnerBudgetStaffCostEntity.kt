package io.cloudflight.jems.server.project.entity.partner.budget.staff_cost

import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetBase
import io.cloudflight.jems.server.project.service.partner.model.StaffCostType
import io.cloudflight.jems.server.project.service.partner.model.StaffCostUnitType
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_staff_cost")
data class ProjectPartnerBudgetStaffCostEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override var id: Long = 0,

    @Embedded
    @field:NotNull
    override val baseProperties: BaseBudgetProperties,

    @Column
    @field:NotNull
    val pricePerUnit: BigDecimal,

    @Column
    val unitType: StaffCostUnitType?,

    @Column
    val type: StaffCostType?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "budgetTranslation.budget")
    val translatedValues: MutableSet<ProjectPartnerBudgetStaffCostTranslEntity> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "budgetPeriodId.budget")
    val budgetPeriodEntities: MutableSet<ProjectPartnerBudgetStaffCostPeriodEntity>

) : ProjectPartnerBudgetBase {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetStaffCostEntity &&
            id > 0 &&
            id == other.id

    override fun hashCode() =
        if (id > 0) id.toInt() else super.hashCode()

}
