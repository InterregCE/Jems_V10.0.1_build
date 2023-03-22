package io.cloudflight.jems.server.project.service.report.project.financialOverview

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdownLine
import java.math.BigDecimal

interface ProjectReportCertificateUnitCostPersistence {

    fun getUnitCosts(projectId: Long, reportId: Long): List<CertificateUnitCostBreakdownLine>
    fun getUnitCostsCumulative(reportIds: Set<Long>): Map<Long, BigDecimal>
    fun updateCurrentlyReportedValues(projectId: Long, reportId: Long, currentValues: Map<Long, BigDecimal>)
}
