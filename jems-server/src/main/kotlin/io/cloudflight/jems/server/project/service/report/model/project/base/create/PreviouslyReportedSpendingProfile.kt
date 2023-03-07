package io.cloudflight.jems.server.project.service.report.model.project.base.create

import java.math.BigDecimal

data class PreviouslyReportedSpendingProfile(
    val partnerId: Long,
    val previouslyReported: BigDecimal,
)
