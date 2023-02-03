package io.cloudflight.jems.server.project.service.report.model.project.identification

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.model.partner.identification.ProjectPartnerReportPeriod
import java.math.BigDecimal

data class ProjectPartnerReportIdentificationSummary(
    val id: Long,
    val reportNumber: Int,
    val partnerNumber: Int,
    val partnerRole: ProjectPartnerRole,
    val partnerId: Long,
    val sumTotalEligibleAfterControl: BigDecimal,
    val nextReportForecast: BigDecimal,
    val periodDetail: ProjectPartnerReportPeriod?
)
