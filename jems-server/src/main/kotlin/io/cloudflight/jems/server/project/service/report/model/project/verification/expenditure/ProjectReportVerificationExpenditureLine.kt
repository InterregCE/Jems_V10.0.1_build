package io.cloudflight.jems.server.project.service.report.model.project.verification.expenditure

import java.math.BigDecimal

data class ProjectReportVerificationExpenditureLine(
    val expenditure: ProjectPartnerReportExpenditureItem,
    val partOfVerificationSample: Boolean,
    val deductedByJs: BigDecimal,
    val deductedByMa: BigDecimal,
    val amountAfterVerification: BigDecimal,
    val typologyOfErrorId: Long?,
    val parked: Boolean,
    val verificationComment: String?
)
