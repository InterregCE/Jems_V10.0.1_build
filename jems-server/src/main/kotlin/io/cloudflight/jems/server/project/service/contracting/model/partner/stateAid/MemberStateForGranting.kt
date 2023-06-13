package io.cloudflight.jems.server.project.service.contracting.model.partner.stateAid

import java.math.BigDecimal

data class MemberStateForGranting(
    val partnerId: Long,
    val country: String,
    val countryCode: String,
    val selected: Boolean,
    val amountInEur: BigDecimal?
)
