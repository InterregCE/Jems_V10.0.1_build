package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.lumpSum.ExpenditureLumpSumCurrentWithReIncluded
import java.math.BigDecimal

interface ProjectPartnerReportLumpSumPersistence {

    fun getLumpSum(partnerId: Long, reportId: Long): List<ExpenditureLumpSumBreakdownLine>

    fun getLumpSumCumulative(reportIds: Set<Long>): Map<Int, ExpenditureLumpSumCurrent>

    fun updateCurrentlyReportedValues(partnerId: Long, reportId: Long, currentlyReported: Map<Long, ExpenditureLumpSumCurrentWithReIncluded>)

    fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControl: Map<Long, ExpenditureLumpSumCurrent>,
    )

    fun getLumpSumCumulativeAfterControl(reportIds: Set<Long>): Map<Int, BigDecimal>
}
