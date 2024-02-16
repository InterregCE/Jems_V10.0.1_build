package io.cloudflight.jems.server.project.service.report.model.project.financialOverview.perPartner

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

data class PartnerCertificateBreakdownLine(
    val partnerId: Long,
    val partnerNumber: Int,
    val partnerRole: ProjectPartnerRole?,
    val partnerOrganization: String?,
    val partnerCountry: String?,
    var totalEligibleBudget: BigDecimal,
    var previouslyReported: BigDecimal,
    var currentReported: BigDecimal,
    var totalReportedSoFar: BigDecimal,
    var totalReportedSoFarPercentage: BigDecimal,
    var remainingBudget: BigDecimal
)
