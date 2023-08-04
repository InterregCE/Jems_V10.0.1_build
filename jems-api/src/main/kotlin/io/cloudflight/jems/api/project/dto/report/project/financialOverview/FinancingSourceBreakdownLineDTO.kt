package io.cloudflight.jems.api.project.dto.report.project.financialOverview

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import java.math.BigDecimal

data class FinancingSourceBreakdownLineDTO(
    val partnerReportId: Long?,
    val partnerReportNumber: Int?,

    val partnerId: Long?,
    val partnerRole: ProjectPartnerRoleDTO?,
    val partnerNumber: Int?,

    val fundsSorted: List<FinancingSourceFundDTO>,

    val partnerContribution: BigDecimal,
    val publicContribution: BigDecimal,
    val automaticPublicContribution: BigDecimal,
    val privateContribution: BigDecimal,

    val total: BigDecimal,

    var split: List<FinancingSourceBreakdownSplitLineDTO>,
)
