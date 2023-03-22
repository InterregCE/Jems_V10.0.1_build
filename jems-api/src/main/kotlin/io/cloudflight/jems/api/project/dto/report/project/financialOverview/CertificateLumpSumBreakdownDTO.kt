package io.cloudflight.jems.api.project.dto.report.project.financialOverview

data class CertificateLumpSumBreakdownDTO(
    val lumpSums: List<CertificateLumpSumBreakdownLineDTO>,
    val total: CertificateLumpSumBreakdownLineDTO,
)
