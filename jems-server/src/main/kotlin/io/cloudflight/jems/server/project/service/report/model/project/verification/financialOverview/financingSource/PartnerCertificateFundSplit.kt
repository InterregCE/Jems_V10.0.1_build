package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource

import java.math.BigDecimal

data class PartnerCertificateFundSplit(
    val partnerReportId: Long,
    val partnerId: Long,
    val fundId: Long,
    val value: BigDecimal,

    val total: BigDecimal,
)
