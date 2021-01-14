package io.cloudflight.jems.server.project.entity.partner.budget

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_equipment")
data class ProjectPartnerBudgetEquipmentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @Column
    override val investmentId: Long?,

    @Embedded
    @field:NotNull
    override val baseProperties: BaseBudgetProperties,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "budgetTranslation.budget")
    override val translatedValues: MutableSet<ProjectPartnerBudgetEquipmentTransl> = mutableSetOf()

) : ProjectPartnerBudgetGeneralBase {

    override fun equals(other: Any?) =
        this === other ||
            other !== null &&
            other is ProjectPartnerBudgetEquipmentEntity &&
            id > 0 &&
            id == other.id

    override fun hashCode() =
        if (id > 0) id.toInt() else super.hashCode()

}
