package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.entity.TranslationBudgetId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * project partner budget equipment lang table
 */
@Entity(name = "project_partner_budget_equipment_transl")
data class ProjectPartnerBudgetEquipmentTransl(

    @EmbeddedId
    val translationId: TranslationBudgetId,

    val description: String? = null

)
