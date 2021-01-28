package io.cloudflight.jems.server.project.entity.partner.budget.travel

import io.cloudflight.jems.server.project.entity.partner.budget.*
import java.math.BigDecimal
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_travel")
data class ProjectPartnerBudgetTravelEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @Embedded
    @field:NotNull
    override val baseProperties: BaseBudgetProperties,

    @Column
    @field:NotNull
    val pricePerUnit: BigDecimal,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "budgetTranslation.budget")
    val translatedValues: MutableSet<ProjectPartnerBudgetTravelTranslEntity> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "budgetPeriodId.budget")
    val budgetPeriodEntities: MutableSet<ProjectPartnerBudgetTravelPeriodEntity>

    ): ProjectPartnerBudgetBase {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetTravelEntity &&
            id > 0 &&
            id == other.id

    override fun hashCode() =
        if (id > 0) id.toInt() else super.hashCode()

}
