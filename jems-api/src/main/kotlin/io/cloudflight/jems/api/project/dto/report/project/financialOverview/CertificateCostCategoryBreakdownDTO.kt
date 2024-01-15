package io.cloudflight.jems.api.project.dto.report.project.financialOverview

data class CertificateCostCategoryBreakdownDTO(
    val staff: CertificateCostCategoryBreakdownLineDTO,
    val office: CertificateCostCategoryBreakdownLineDTO,
    val travel: CertificateCostCategoryBreakdownLineDTO,
    val external: CertificateCostCategoryBreakdownLineDTO,
    val equipment: CertificateCostCategoryBreakdownLineDTO,
    val infrastructure: CertificateCostCategoryBreakdownLineDTO,
    val other: CertificateCostCategoryBreakdownLineDTO,
    val lumpSum: CertificateCostCategoryBreakdownLineDTO,
    val unitCost: CertificateCostCategoryBreakdownLineDTO,
    val spfCost: CertificateCostCategoryBreakdownLineDTO,
    val total: CertificateCostCategoryBreakdownLineDTO,
)
