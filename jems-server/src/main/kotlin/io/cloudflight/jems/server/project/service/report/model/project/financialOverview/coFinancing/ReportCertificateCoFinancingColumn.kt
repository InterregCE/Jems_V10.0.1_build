package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing

import java.math.BigDecimal

data class ReportCertificateCoFinancingColumn(
    val funds: Map<Long?, BigDecimal>,
    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val sum: BigDecimal,
)
