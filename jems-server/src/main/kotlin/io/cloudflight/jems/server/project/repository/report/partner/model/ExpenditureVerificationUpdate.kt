package io.cloudflight.jems.server.project.repository.report.partner.model

import java.math.BigDecimal

data class ExpenditureVerificationUpdate(
    val id: Long,
    val partOfSample: Boolean,
    val certifiedAmount: BigDecimal,
    val deductedAmount: BigDecimal,
    val typologyOfErrorId: Long?,
    val verificationComment: String?
)
