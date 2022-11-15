package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import java.math.BigDecimal

interface ProjectReportUnitCostPersistence {

    fun getUnitCost(partnerId: Long, reportId: Long): List<ExpenditureUnitCostBreakdownLine>

    fun getUnitCostCumulative(reportIds: Set<Long>): Map<Long, BigDecimal>

    fun updateCurrentlyReportedValues(partnerId: Long, reportId: Long, currentlyReported: Map<Long, BigDecimal>)

}
