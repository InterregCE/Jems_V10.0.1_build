package io.cloudflight.jems.server.project.entity.partner.budget.spf

import io.cloudflight.jems.server.common.entity.TranslationEntity
import io.cloudflight.jems.server.common.entity.TranslationId
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_spfcost_transl")
class ProjectPartnerBudgetSpfCostTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerBudgetSpfCostEntity>,

    val unitType: String? = null,

    val description: String? = null,

    val comments: String? = null

) : TranslationEntity()
