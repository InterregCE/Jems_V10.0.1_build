package io.cloudflight.jems.server.project.service.lumpsum.model

import java.math.BigDecimal

data class ProjectPartnerLumpSum(
    val partnerId: Long,
    val amount: BigDecimal,
)
