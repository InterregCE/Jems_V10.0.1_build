package io.cloudflight.jems.api.project.dto.report.project.financialOverview

data class CertificateUnitCostBreakdownDTO(
    val unitCosts: List<CertificateUnitCostBreakdownLineDTO>,
    val total: CertificateUnitCostBreakdownLineDTO,
)
