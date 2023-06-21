package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum

data class CertificateLumpSumBreakdown(
    val lumpSums: List<CertificateLumpSumBreakdownLine>,
    val total: CertificateLumpSumBreakdownLine,
)
