package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost

data class CertificateUnitCostBreakdown(
    val unitCosts: List<CertificateUnitCostBreakdownLine>,
    val total: CertificateUnitCostBreakdownLine,
)
