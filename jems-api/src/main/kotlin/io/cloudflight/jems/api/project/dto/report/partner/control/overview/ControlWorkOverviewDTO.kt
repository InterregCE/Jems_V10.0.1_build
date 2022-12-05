package io.cloudflight.jems.api.project.dto.report.partner.control.overview

import java.math.BigDecimal

data class ControlWorkOverviewDTO(
    val declaredByPartner: BigDecimal,
    val inControlSample: BigDecimal,
    val parked: BigDecimal,
    val deductedByControl: BigDecimal,
    val eligibleAfterControl: BigDecimal,
    val eligibleAfterControlPercentage: BigDecimal,
)
