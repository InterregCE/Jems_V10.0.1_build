package io.cloudflight.jems.server.project.service.report.model.partner.expenditure

import java.math.BigDecimal

data class ProjectPartnerControlReportExpenditureVerificationUpdate(
    val id: Long,
    val partOfSample: Boolean,
    val certifiedAmount: BigDecimal,
    val deductedAmount: BigDecimal,
    val typologyOfErrorId: Long?,
    val verificationComment: String?
)
