package io.cloudflight.jems.server.project.repository.report.project.coFinancing

import java.math.BigDecimal

data class ReportCertificateCoFinancingColumnWithoutFunds(
    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,
    val sum: BigDecimal,
)
