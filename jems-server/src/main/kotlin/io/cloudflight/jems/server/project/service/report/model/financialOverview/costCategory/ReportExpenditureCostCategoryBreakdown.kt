package io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory

data class ReportExpenditureCostCategoryBreakdown(
    val staff: ReportExpenditureCostCategoryLine,
    val office: ReportExpenditureCostCategoryLine,
    val travel: ReportExpenditureCostCategoryLine,
    val external: ReportExpenditureCostCategoryLine,
    val equipment: ReportExpenditureCostCategoryLine,
    val infrastructure: ReportExpenditureCostCategoryLine,
    val other: ReportExpenditureCostCategoryLine,
    val lumpSum: ReportExpenditureCostCategoryLine,
    val unitCost: ReportExpenditureCostCategoryLine,
)
