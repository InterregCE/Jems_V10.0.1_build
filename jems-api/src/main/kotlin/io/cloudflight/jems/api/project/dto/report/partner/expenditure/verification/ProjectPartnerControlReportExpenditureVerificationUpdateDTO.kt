package io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification

import java.math.BigDecimal

data class ProjectPartnerControlReportExpenditureVerificationUpdateDTO(
    val id: Long,
    val partOfSample: Boolean,
    val certifiedAmount: BigDecimal,
    val deductedAmount: BigDecimal,
    val typologyOfErrorId: Long?,
    val verificationComment: String?,
)
