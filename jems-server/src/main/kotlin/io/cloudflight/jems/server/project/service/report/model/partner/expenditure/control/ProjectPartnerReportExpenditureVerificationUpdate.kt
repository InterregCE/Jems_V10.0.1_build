package io.cloudflight.jems.server.project.service.report.model.partner.expenditure.control

import java.math.BigDecimal

data class ProjectPartnerReportExpenditureVerificationUpdate(
    val id: Long,
    val partOfSample: Boolean,
    val certifiedAmount: BigDecimal,
    val typologyOfErrorId: Long?,
    val parked: Boolean,
    val verificationComment: String?
)
