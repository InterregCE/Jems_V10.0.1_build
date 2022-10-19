package io.cloudflight.jems.server.project.service.report.model.financialOverview.lumpSum

data class ExpenditureLumpSumBreakdown(
    val lumpSums: List<ExpenditureLumpSumBreakdownLine>,
    val total: ExpenditureLumpSumBreakdownLine,
)
