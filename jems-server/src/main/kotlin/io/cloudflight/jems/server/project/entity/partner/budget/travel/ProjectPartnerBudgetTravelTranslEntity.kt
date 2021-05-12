package io.cloudflight.jems.server.project.entity.partner.budget.travel

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_travel_transl")
data class ProjectPartnerBudgetTravelTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerBudgetTravelEntity>,

    val unitType: String? = null,

    val description: String? = null

) : TranslationEntity()
