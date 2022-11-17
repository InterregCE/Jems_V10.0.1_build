package io.cloudflight.jems.api.project.dto.report.partner.financialOverview

data class ExpenditureLumpSumBreakdownDTO(
    val lumpSums: List<ExpenditureLumpSumBreakdownLineDTO>,
    val total: ExpenditureLumpSumBreakdownLineDTO,
)
