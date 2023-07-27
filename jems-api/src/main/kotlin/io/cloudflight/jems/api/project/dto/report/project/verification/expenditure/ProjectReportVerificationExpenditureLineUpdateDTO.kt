package io.cloudflight.jems.api.project.dto.report.project.verification.expenditure

import java.math.BigDecimal

data class ProjectReportVerificationExpenditureLineUpdateDTO (
    val expenditureId: Long,
    val partOfVerificationSample: Boolean,
    val deductedByJs: BigDecimal,
    val deductedByMa: BigDecimal,
    val amountAfterVerification: BigDecimal,
    val typologyOfErrorId: Long?,
    val parked: Boolean,
    val verificationComment: String?
)
