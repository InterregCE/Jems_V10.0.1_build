package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory

data class CertificateCostCategoryBreakdown(
    val staff: CertificateCostCategoryBreakdownLine,
    val office: CertificateCostCategoryBreakdownLine,
    val travel: CertificateCostCategoryBreakdownLine,
    val external: CertificateCostCategoryBreakdownLine,
    val equipment: CertificateCostCategoryBreakdownLine,
    val infrastructure: CertificateCostCategoryBreakdownLine,
    val other: CertificateCostCategoryBreakdownLine,
    val lumpSum: CertificateCostCategoryBreakdownLine,
    val unitCost: CertificateCostCategoryBreakdownLine,
    val total: CertificateCostCategoryBreakdownLine,
)
