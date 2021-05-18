package io.cloudflight.jems.server.project.entity.partner.budget.general.infrastructure

import io.cloudflight.jems.server.common.entity.TranslationId
import io.cloudflight.jems.server.project.entity.partner.budget.general.ProjectPartnerBudgetGeneralTranslBase
import javax.persistence.EmbeddedId
import javax.persistence.Entity

@Entity(name = "project_partner_budget_infrastructure_transl")
data class ProjectPartnerBudgetInfrastructureTranslEntity(

    @EmbeddedId
    override val translationId: TranslationId<ProjectPartnerBudgetInfrastructureEntity>,

    override val awardProcedures: String? = null,

    override val unitType: String? = null,

    override val description: String? = null

) : ProjectPartnerBudgetGeneralTranslBase()
