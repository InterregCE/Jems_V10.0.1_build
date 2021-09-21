package io.cloudflight.jems.server.project.service.cofinancing.model

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.math.BigDecimal

data class PartnerBudgetCoFinancing(

    val partner: ProjectPartnerSummary,
    val projectPartnerCoFinancingAndContribution: ProjectPartnerCoFinancingAndContribution? = null,
    val total: BigDecimal? = null

)
