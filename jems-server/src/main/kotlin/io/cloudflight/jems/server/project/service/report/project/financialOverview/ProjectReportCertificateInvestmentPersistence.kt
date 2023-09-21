package io.cloudflight.jems.server.project.service.report.project.financialOverview

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdownLine
import java.math.BigDecimal

interface ProjectReportCertificateInvestmentPersistence {

    fun getInvestments(projectId: Long, reportId: Long): List<CertificateInvestmentBreakdownLine>

    fun getReportedInvestmentCumulative(reportIds: Set<Long>): Map<Long, BigDecimal>

    fun getVerifiedInvestmentCumulative(reportIds: Set<Long>): Map<Long, BigDecimal>

    fun updateCurrentlyReportedValues(projectId: Long, reportId: Long, currentValues: Map<Long, BigDecimal>)
}
