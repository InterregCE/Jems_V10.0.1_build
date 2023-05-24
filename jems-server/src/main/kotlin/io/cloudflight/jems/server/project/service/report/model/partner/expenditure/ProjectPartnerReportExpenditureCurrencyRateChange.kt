package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import java.math.BigDecimal

data class ProjectPartnerReportExpenditureCurrencyRateChange(
    val id: Long,
    val currencyConversionRate: BigDecimal?,
    val declaredAmountAfterSubmission: BigDecimal?,
)
