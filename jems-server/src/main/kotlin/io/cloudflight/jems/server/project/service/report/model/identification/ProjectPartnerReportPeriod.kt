package io.cloudflight.jems.server.project.service.report.model.identification

import java.math.BigDecimal

data class ProjectPartnerReportPeriod(
    val number: Int,
    val periodBudget: BigDecimal,
    val periodBudgetCumulative: BigDecimal,
    val start: Int,
    val end: Int,
)
