package io.cloudflight.jems.api.project.dto.report.project.identification

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.report.partner.identification.ProjectPartnerReportPeriodDTO
import java.math.BigDecimal

data class ProjectReportSpendingProfileDTO(
    val partnerRole: ProjectPartnerRoleDTO,
    val partnerNumber: Int,
    val periodDetail: ProjectPartnerReportPeriodDTO?,
    var currentReport: BigDecimal,
    val previouslyReported: BigDecimal,
    var differenceFromPlan: BigDecimal,
    var differenceFromPlanPercentage: BigDecimal,
    val nextReportForecast: BigDecimal,
)

