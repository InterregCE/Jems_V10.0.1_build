package io.cloudflight.jems.server.project.service.report.model.partner.base.create

import java.math.BigDecimal

data class PreviouslyReportedFund(
    val fundId: Long?,
    val percentage: BigDecimal,
    val percentageSpf: BigDecimal,
    val total: BigDecimal,
    val previouslyReported: BigDecimal,
    val previouslyReportedParked: BigDecimal,
    val previouslyValidated: BigDecimal,
    val previouslyPaid: BigDecimal,
    val disabled: Boolean,
)
