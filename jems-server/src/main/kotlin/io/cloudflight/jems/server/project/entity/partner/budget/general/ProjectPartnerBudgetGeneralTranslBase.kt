package io.cloudflight.jems.server.project.entity.partner.budget.general

import io.cloudflight.jems.server.project.entity.partner.budget.ProjectPartnerBudgetTranslBase

interface ProjectPartnerBudgetGeneralTranslBase :
    ProjectPartnerBudgetTranslBase {
    val awardProcedures: String?
    val unitType: String?
    val description: String?
}
