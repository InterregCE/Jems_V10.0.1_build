package io.cloudflight.jems.api.project.dto.lumpsum

import java.math.BigDecimal

data class ProjectPartnerLumpSumDTO (
    val partnerId: Long,
    val amount: BigDecimal = BigDecimal.ZERO
)
