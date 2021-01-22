package io.cloudflight.jems.server.project.service.cofinancing.model

import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerCoFinancingAndContribution
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartner
import java.math.BigDecimal

data class PartnerBudgetCoFinancing(

    val partner: ProjectPartner? = null,
    val projectPartnerCoFinancingAndContribution: ProjectPartnerCoFinancingAndContribution? = null,
    val total: BigDecimal? = null

)