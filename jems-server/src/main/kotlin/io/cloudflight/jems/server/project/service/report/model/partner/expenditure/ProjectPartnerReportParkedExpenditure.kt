package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import io.cloudflight.jems.api.project.dto.InputTranslation

data class ProjectPartnerReportParkedExpenditure(
    val expenditure: ProjectPartnerReportExpenditureCost,

    val lumpSum: ProjectPartnerReportParkedLinked?,
    val lumpSumName: Set<InputTranslation>?,
    val unitCost: ProjectPartnerReportParkedLinked?,
    val unitCostName: Set<InputTranslation>?,
    val investment: ProjectPartnerReportParkedLinked?,
    val investmentName: String?,
)
