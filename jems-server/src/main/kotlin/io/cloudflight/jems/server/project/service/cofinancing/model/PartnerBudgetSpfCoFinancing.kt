package io.cloudflight.jems.server.project.service.cofinancing.model

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContributionSpf
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import java.math.BigDecimal

data class PartnerBudgetSpfCoFinancing(

    val partner: ProjectPartnerSummary,
    val projectPartnerCoFinancingAndContribution: ProjectPartnerCoFinancingAndContributionSpf,
    val total: BigDecimal? = null

)
