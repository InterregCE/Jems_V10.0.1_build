package io.cloudflight.jems.api.project.dto.report.project.identification

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal

class ProjectReportSpendingProfileLineDto(
    val partnerRole: ProjectPartnerRoleDTO,
    val partnerNumber: Int,
    val partnerAbbreviation: String,
    val partnerCountry: String,
    val periodBudget: BigDecimal,
    val periodBudgetCumulative: BigDecimal,
    var currentReport: BigDecimal,
    val previouslyReported: BigDecimal,
    var differenceFromPlan: BigDecimal,
    var differenceFromPlanPercentage: BigDecimal,
    val nextReportForecast: BigDecimal,
    val totalEligibleBudget: BigDecimal,
    val totalReportedSoFar: BigDecimal,
    val totalReportedSoFarPercentage: BigDecimal?,
    val remainingBudget: BigDecimal?,
)
