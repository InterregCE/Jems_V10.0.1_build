package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

data class PartnerCertificateFundSplit(
    val partnerReportId: Long,
    val partnerReportNumber: Int,

    val partnerId: Long,
    val partnerRole: ProjectPartnerRole,
    val partnerNumber: Int,

    val fundId: Long,
    val value: BigDecimal,

    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,

    val total: BigDecimal,
)
