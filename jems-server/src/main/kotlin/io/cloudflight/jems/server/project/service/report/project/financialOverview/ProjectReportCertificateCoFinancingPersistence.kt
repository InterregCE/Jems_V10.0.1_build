package io.cloudflight.jems.server.project.service.report.project.financialOverview

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn

interface ProjectReportCertificateCoFinancingPersistence {
    fun getCoFinancing(projectId: Long, reportId: Long): ReportCertificateCoFinancing

    fun getCoFinancingCumulative(reportIds: Set<Long>): ReportCertificateCoFinancingColumn

    fun updateCurrentlyReportedValues(projectId: Long, reportId: Long, currentlyReported: ReportCertificateCoFinancingColumn)

}
