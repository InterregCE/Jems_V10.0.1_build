package io.cloudflight.jems.api.project.dto.report.project.financialOverview

data class CertificateCoFinancingBreakdownDTO(
    val funds: List<CertificateCoFinancingBreakdownLineDTO>,
    val partnerContribution: CertificateCoFinancingBreakdownLineDTO,
    val publicContribution: CertificateCoFinancingBreakdownLineDTO,
    val automaticPublicContribution: CertificateCoFinancingBreakdownLineDTO,
    val privateContribution: CertificateCoFinancingBreakdownLineDTO,
    val total: CertificateCoFinancingBreakdownLineDTO,
)
