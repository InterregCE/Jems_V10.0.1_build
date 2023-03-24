package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment

data class CertificateInvestmentBreakdown(
    val investments: List<CertificateInvestmentBreakdownLine>,
    val total: CertificateInvestmentBreakdownLine
)
