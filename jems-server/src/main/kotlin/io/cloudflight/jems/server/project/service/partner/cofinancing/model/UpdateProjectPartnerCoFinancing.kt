package io.cloudflight.jems.server.project.service.partner.cofinancing.model

import java.math.BigDecimal

data class UpdateProjectPartnerCoFinancing(
    val fundId: Long? = null,
    val percentage: BigDecimal? = null,
)
