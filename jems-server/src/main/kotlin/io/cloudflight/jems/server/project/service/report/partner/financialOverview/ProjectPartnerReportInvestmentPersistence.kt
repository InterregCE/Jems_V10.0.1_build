package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.investments.ExpenditureInvestmentCurrentWithReIncluded
import java.math.BigDecimal

interface ProjectPartnerReportInvestmentPersistence {
    fun getInvestments(partnerId: Long, reportId: Long): List<ExpenditureInvestmentBreakdownLine>

    fun getInvestmentsCumulative(reportIds: Set<Long>): Map<Long, ExpenditureInvestmentCurrent>

    fun updateCurrentlyReportedValues(partnerId: Long, reportId: Long, currentlyReported: Map<Long, ExpenditureInvestmentCurrentWithReIncluded>)

    fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControl: Map<Long, ExpenditureInvestmentCurrent>,
    )

    fun getInvestmentsCumulativeAfterControl(reportIds: Set<Long>): Map<Long, BigDecimal>
}
