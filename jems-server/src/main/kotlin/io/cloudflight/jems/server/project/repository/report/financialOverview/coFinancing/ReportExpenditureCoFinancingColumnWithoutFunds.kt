package io.cloudflight.jems.server.project.repository.report.financialOverview.coFinancing

import java.math.BigDecimal

data class ReportExpenditureCoFinancingColumnWithoutFunds(
    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val sum: BigDecimal,
)
