package io.cloudflight.jems.server.project.service.report.model.project.identification

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal

data class ProjectReportSpendingProfile(
    val partnerRole: ProjectPartnerRole,
    val partnerNumber: Int,
    val periodDetail: ProjectPartnerReportPeriod?,
    var currentReport: BigDecimal,
    val previouslyReported: BigDecimal,
    var differenceFromPlan: BigDecimal,
    var differenceFromPlanPercentage: BigDecimal,
    val nextReportForecast: BigDecimal,
)
