package io.cloudflight.jems.server.project.service.report.partner.financialOverview

import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostBreakdownLine
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrent
import io.cloudflight.jems.server.project.service.report.model.partner.financialOverview.unitCost.ExpenditureUnitCostCurrentWithReIncluded
import java.math.BigDecimal

interface ProjectPartnerReportUnitCostPersistence {

    fun getUnitCost(partnerId: Long, reportId: Long): List<ExpenditureUnitCostBreakdownLine>

    fun getUnitCostCumulative(reportIds: Set<Long>): Map<Long, ExpenditureUnitCostCurrent>

    fun updateCurrentlyReportedValues(partnerId: Long, reportId: Long, currentlyReported: Map<Long, ExpenditureUnitCostCurrentWithReIncluded>)

    fun updateAfterControlValues(
        partnerId: Long,
        reportId: Long,
        afterControl: Map<Long, ExpenditureUnitCostCurrent>,
    )

    fun getUnitCostCumulativeAfterControl(reportIds: Set<Long>): Map<Long, BigDecimal>
}
