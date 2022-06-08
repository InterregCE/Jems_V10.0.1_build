package io.cloudflight.jems.server.project.service.report.model.identification

import java.math.BigDecimal

data class ProjectPartnerReportSpendingProfile(
    val periodDetail: ProjectPartnerReportPeriod?,
    var currentReport: BigDecimal,
    val previouslyReported: BigDecimal,
    var differenceFromPlan: BigDecimal,
    var differenceFromPlanPercentage: BigDecimal,
    val nextReportForecast: BigDecimal,
)
