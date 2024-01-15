package io.cloudflight.jems.server.project.service.report.model.partner.identification

import io.cloudflight.jems.server.project.service.model.ProjectPeriodBase
import java.math.BigDecimal
import java.time.LocalDate

data class ProjectPartnerReportPeriod(
    override val number: Int,
    val periodBudget: BigDecimal,
    val periodBudgetCumulative: BigDecimal,
    override val start: Int,
    override val end: Int,
    override var startDate: LocalDate? = null,
    override var endDate: LocalDate? = null,
) : ProjectPeriodBase
