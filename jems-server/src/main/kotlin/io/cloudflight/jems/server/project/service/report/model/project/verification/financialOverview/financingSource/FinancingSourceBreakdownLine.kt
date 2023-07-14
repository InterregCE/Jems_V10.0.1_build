package io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import java.math.BigDecimal

data class FinancingSourceBreakdownLine(
    val partnerReportId: Long,
    val partnerReportNumber: Int,

    val partnerId: Long,
    val partnerRole: ProjectPartnerRole,
    val partnerNumber: Int,

    val fundsSorted: List<Pair<ProgrammeFund, BigDecimal>>,

    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,

    val total: BigDecimal,

    val split: List<FinancingSourceBreakdownSplitLine>,
)
