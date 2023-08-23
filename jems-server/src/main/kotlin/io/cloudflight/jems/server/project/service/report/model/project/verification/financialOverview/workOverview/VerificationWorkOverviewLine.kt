package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview

import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

data class VerificationWorkOverviewLine(
    val partnerId: Long?,
    val partnerRole: ProjectPartnerRole?,
    val partnerNumber: Int?,

    val partnerReportId: Long?,
    val partnerReportNumber: Int?,

    val requestedByPartner: BigDecimal,
    val requestedByPartnerWithoutFlatRates: BigDecimal,
    val inVerificationSample: BigDecimal,
    val inVerificationSamplePercentage: BigDecimal?,
    val parked: BigDecimal,
    val deductedByJs: BigDecimal,
    val deductedByMa: BigDecimal,
    val deducted: BigDecimal,
    val afterVerification: BigDecimal,
    val afterVerificationPercentage: BigDecimal?,
)
