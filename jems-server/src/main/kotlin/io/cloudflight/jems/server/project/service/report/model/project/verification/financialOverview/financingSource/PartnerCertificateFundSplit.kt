package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource

import java.math.BigDecimal

data class PartnerCertificateFundSplit(
    val partnerReportId: Long?, // null in case of SPF
    val partnerId: Long,
    val fundId: Long,
    val value: BigDecimal,

    val defaultPartnerContribution: BigDecimal,
    val defaultOfWhichPublic: BigDecimal,
    val defaultOfWhichAutoPublic: BigDecimal,
    val defaultOfWhichPrivate: BigDecimal,

    val total: BigDecimal,
)
