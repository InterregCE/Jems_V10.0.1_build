package io.cloudflight.jems.api.project.dto.report.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerReportParkedExpenditureDTO(
    val expenditure: ProjectPartnerReportExpenditureCostDTO,

    val unitCost: ProjectPartnerReportParkedLinkedDTO?,
    val unitCostName: Set<InputTranslation>?,
    val lumpSum: ProjectPartnerReportParkedLinkedDTO?,
    val lumpSumName: Set<InputTranslation>?,
    val investment: ProjectPartnerReportParkedLinkedDTO?,
    val investmentName: String?,
)
