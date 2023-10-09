package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import java.math.BigDecimal

data class ProjectPartnerCoFinancingWithSpfPart(
    val fundId: Long?,
    val percentage: BigDecimal,
    val percentageSpf: BigDecimal,
)
