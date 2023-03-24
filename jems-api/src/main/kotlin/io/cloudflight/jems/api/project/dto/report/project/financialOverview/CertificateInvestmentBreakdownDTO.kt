package io.cloudflight.jems.api.project.dto.report.project.financialOverview

data class CertificateInvestmentBreakdownDTO(
    val investments: List<CertificateInvestmentBreakdownLineDTO>,
    val total: CertificateInvestmentBreakdownLineDTO
)
