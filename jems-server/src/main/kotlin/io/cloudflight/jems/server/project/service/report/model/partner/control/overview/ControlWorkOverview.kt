package io.cloudflight.jems.server.project.service.report.model.partner.control.overview

import java.math.BigDecimal

data class ControlWorkOverview(
    val declaredByPartner: BigDecimal,
    val inControlSample: BigDecimal,
    val inControlSamplePercentage: BigDecimal,
    val parked: BigDecimal,
    val deductedByControl: BigDecimal,
    val eligibleAfterControl: BigDecimal,
    val eligibleAfterControlPercentage: BigDecimal,
)
