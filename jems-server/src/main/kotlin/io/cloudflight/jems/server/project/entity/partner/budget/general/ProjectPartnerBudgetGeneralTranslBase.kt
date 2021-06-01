package io.cloudflight.jems.server.project.entity.partner.budget.general

import io.cloudflight.jems.server.common.entity.TranslationEntity

abstract class ProjectPartnerBudgetGeneralTranslBase : TranslationEntity() {
    abstract val awardProcedures: String?
    abstract val unitType: String?
    abstract val description: String?
}
