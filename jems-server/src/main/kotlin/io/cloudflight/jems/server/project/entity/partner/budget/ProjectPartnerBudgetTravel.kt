package io.cloudflight.jems.server.project.entity.partner.budget

import javax.persistence.CascadeType
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.validation.constraints.NotNull

@Entity(name = "project_partner_budget_travel")
data class ProjectPartnerBudgetTravel(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long = 0,

    @field:NotNull
    override val partnerId: Long,

    @Embedded
    override val budget: Budget,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, mappedBy = "translationId.partnerId")
    val translatedValues: Set<ProjectPartnerBudgetTravelTransl> = emptySet()

): CommonBudget
