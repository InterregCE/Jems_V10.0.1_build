package io.cloudflight.jems.server.project.entity.partner.budget

import io.cloudflight.jems.server.project.entity.TranslationPartnerId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

/**
 * project partner budget infrastructure lang table
 */
@Entity(name = "project_partner_budget_infrastructure_transl")
data class ProjectPartnerBudgetInfrastructureTransl(

    @EmbeddedId
    val translationId: TranslationPartnerId,

    val description: String? = null

)
