package io.cloudflight.jems.api.project.dto.cofinancing

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import java.math.BigDecimal

data class ProjectPartnerBudgetCoFinancingDTO (

    val partner: ProjectPartnerSummaryDTO,
    val projectPartnerCoFinancingAndContributionOutputDTO: ProjectPartnerCoFinancingAndContributionOutputDTO? = null,
    val total: BigDecimal? = null

)
