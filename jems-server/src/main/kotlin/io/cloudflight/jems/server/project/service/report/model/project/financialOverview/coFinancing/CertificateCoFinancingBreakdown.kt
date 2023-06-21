package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing

data class CertificateCoFinancingBreakdown(
    val funds: List<CertificateCoFinancingBreakdownLine>,
    val partnerContribution: CertificateCoFinancingBreakdownLine,
    val publicContribution: CertificateCoFinancingBreakdownLine,
    val automaticPublicContribution: CertificateCoFinancingBreakdownLine,
    val privateContribution: CertificateCoFinancingBreakdownLine,
    val total: CertificateCoFinancingBreakdownLine,
)
