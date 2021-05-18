package io.cloudflight.jems.server.project.entity.partner.budget.general.external

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralTranslBase
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_external_transl")
data class ProjectPartnerBudgetExternalTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerBudgetExternalEntity>,

    override val awardProcedures: String? = null,

    override val unitType: String? = null,

    override val description: String? = null

) : ProjectPartnerBudgetGeneralTranslBase()
