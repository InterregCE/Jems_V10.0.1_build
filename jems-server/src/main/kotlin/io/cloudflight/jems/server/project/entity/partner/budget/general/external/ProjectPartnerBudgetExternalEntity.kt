package io.cloudflight.jems.server.project.entity.partner.budget.general.external

import io.cloudflight.jems.server.project.entity.partner.budget.BaseBudgetProperties
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralBase
import java.math.BigDecimal
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_external")
data class ProjectPartnerBudgetExternalEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @Embedded
    @field:NotNull
    override val baseProperties: BaseBudgetProperties,

    @Column
    override val investmentId: Long?,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "budgetTranslation.budget")
    override var translatedValues: MutableSet<ProjectPartnerBudgetExternalTranslEntity> = mutableSetOf(),

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "budgetPeriodId.budget")
    override val budgetPeriodEntities: MutableSet<ProjectPartnerBudgetExternalPeriodEntity>,

    @Column
    @field:NotNull
    override val pricePerUnit: BigDecimal,

    override val unitCostId: Long?

) : ProjectPartnerBudgetGeneralBase {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetExternalEntity &&
            id > 0 &&
            id == other.id

    override fun hashCode() =
        super.hashCode()

}
