 package io.cloudflight.jems.api.project.dto.contracting.partner

import java.math.BigDecimal

data class MemberStateForGrantingDTO(
    val partnerId: Long,
    val countryCode: String,
    val country: String,
    val selected: Boolean,
    val amountInEur: BigDecimal?
)
