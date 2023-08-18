package io.cloudflight.jems.api.project.dto.report.project.financialOverview.verification

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal

data class VerificationWorkOverviewLineDTO(
    val partnerId: Long?,
    val partnerRole: ProjectPartnerRoleDTO?,
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
