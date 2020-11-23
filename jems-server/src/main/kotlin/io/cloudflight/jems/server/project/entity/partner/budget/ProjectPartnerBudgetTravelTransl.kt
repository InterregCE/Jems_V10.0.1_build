package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.entity.TranslationBudgetId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * project partner budget travel lang table
 */
@Entity(name = "project_partner_budget_travel_transl")
data class ProjectPartnerBudgetTravelTransl(

    @EmbeddedId
    val translationId: TranslationBudgetId,

    val description: String? = null

)
