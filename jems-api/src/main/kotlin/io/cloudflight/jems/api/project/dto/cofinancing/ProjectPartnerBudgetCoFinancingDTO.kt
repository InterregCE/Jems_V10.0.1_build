package io.cloudflight.jems.api.project.dto.cofinancing

import io.cloudflight.jems.api.project.dto.partner.OutputProjectPartner
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerCoFinancingAndContributionOutputDTO
import java.math.BigDecimal

data class ProjectPartnerBudgetCoFinancingDTO (

    val partner: OutputProjectPartner? = null,
    val projectPartnerCoFinancingAndContributionOutputDTO: ProjectPartnerCoFinancingAndContributionOutputDTO? = null,
    val total: BigDecimal? = null

)