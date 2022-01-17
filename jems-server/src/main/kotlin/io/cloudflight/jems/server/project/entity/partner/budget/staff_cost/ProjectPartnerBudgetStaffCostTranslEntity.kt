package io.cloudflight.jems.server.project.entity.partner.budget.staff_cost

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_staff_cost_transl")
data class ProjectPartnerBudgetStaffCostTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerBudgetStaffCostEntity>,

    val description: String? = null,

    val comments: String? = null,

    val unitType: String? = null

): TranslationEntity()
