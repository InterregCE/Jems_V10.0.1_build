package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.project.service.report.model.financialOverview.lumpSum.ExpenditureLumpSumBreakdownLine
import java.math.BigDecimal

interface ProjectReportLumpSumPersistence {

    fun getLumpSum(partnerId: Long, reportId: Long): List<ExpenditureLumpSumBreakdownLine>

    fun getLumpSumCumulative(reportIds: Set<Long>): Map<Int, BigDecimal>

    fun updateCurrentlyReportedValues(partnerId: Long, reportId: Long, currentlyReported: Map<Long, BigDecimal>)

}
