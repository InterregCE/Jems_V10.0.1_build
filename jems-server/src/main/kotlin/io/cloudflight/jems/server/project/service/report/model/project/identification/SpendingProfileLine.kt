package io.cloudflight.jems.server.project.service.report.model.project.identification

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal

data class SpendingProfileLine(
    val partnerRole: ProjectPartnerRole?,
    val partnerNumber: Int?,
    val partnerAbbreviation: String?,
    val partnerCountry: String?,

    val periodDetail: ProjectPartnerReportPeriod?,
    var differenceFromPlan: BigDecimal,
    var differenceFromPlanPercentage: BigDecimal,
    var nextReportForecast: BigDecimal,

    var totalEligibleBudget: BigDecimal,
    var currentReport: BigDecimal,
    var previouslyReported: BigDecimal,
    var totalReportedSoFar: BigDecimal,
    var totalReportedSoFarPercentage: BigDecimal?,
    var remainingBudget: BigDecimal?,
)
