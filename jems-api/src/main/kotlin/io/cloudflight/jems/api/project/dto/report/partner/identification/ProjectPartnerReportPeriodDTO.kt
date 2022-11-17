package io.cloudflight.jems.api.project.dto.report.partner.identification

import java.math.BigDecimal

data class ProjectPartnerReportPeriodDTO(
    val number: Int,
    val periodBudget: BigDecimal,
    val periodBudgetCumulative: BigDecimal,
    val start: Int,
    val end: Int,
)
