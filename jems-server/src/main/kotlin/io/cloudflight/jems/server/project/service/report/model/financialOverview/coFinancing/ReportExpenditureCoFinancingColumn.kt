package io.cloudflight.jems.server.project.service.report.model.financialOverview.coFinancing

import java.math.BigDecimal

data class ReportExpenditureCoFinancingColumn(
    val funds: Map<Long?, BigDecimal>,
    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val sum: BigDecimal,
)
