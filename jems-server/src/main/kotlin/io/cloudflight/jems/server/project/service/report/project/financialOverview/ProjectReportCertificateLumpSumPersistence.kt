package io.cloudflight.jems.server.project.service.report.project.financialOverview

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdownLine
import java.math.BigDecimal

interface ProjectReportCertificateLumpSumPersistence {

    fun getLumpSums(projectId: Long, reportId: Long): List<CertificateLumpSumBreakdownLine>
    fun getLumpSumCumulative(reportIds: Set<Long>): Map<Int, BigDecimal>
    fun updateCurrentlyReportedValues(projectId: Long, reportId: Long, currentValues: Map<Int, BigDecimal>)
}
