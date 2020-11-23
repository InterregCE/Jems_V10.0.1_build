package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.entity.TranslationBudgetId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * project partner budget staff cost lang table
 */
@Entity(name = "project_partner_budget_staff_cost_transl")
data class ProjectPartnerBudgetStaffCostTransl(

    @EmbeddedId
    val translationId: TranslationBudgetId,

    val description: String? = null

)
