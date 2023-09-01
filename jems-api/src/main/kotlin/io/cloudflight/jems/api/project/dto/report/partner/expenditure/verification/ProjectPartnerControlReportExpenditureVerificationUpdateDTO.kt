package io.cloudflight.jems.api.project.dto.report.partner.expenditure.verification

import java.math.BigDecimal

data class ProjectPartnerControlReportExpenditureVerificationUpdateDTO(
    val id: Long,
    val partOfSample: Boolean,
    val deductedAmount: BigDecimal,
    val typologyOfErrorId: Long?,
    val parked: Boolean,
    val verificationComment: String?,
)
