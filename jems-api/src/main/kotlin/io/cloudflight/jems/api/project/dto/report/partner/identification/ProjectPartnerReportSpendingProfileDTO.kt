package io.cloudflight.jems.api.project.dto.report.partner.identification

import java.math.BigDecimal

data class ProjectPartnerReportSpendingProfileDTO(
    val periodDetail: ProjectPartnerReportPeriodDTO?,
    var currentReport: BigDecimal,
    val previouslyReported: BigDecimal,
    var differenceFromPlan: BigDecimal,
    var differenceFromPlanPercentage: BigDecimal,
    val nextReportForecast: BigDecimal,
)
