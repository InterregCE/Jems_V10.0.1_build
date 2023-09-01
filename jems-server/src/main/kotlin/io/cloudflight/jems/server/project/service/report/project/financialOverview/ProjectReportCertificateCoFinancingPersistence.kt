package io.cloudflight.jems.server.project.service.report.project.financialOverview

import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancing
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingColumn
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.coFinancing.ReportCertificateCoFinancingPrevious

interface ProjectReportCertificateCoFinancingPersistence {

    fun getAvailableFunds(reportId: Long): List<ProgrammeFund>

    fun getCoFinancing(projectId: Long, reportId: Long): ReportCertificateCoFinancing

    fun getCoFinancingCumulative(submittedReportIds: Set<Long>, finalizedReportIds: Set<Long>): ReportCertificateCoFinancingPrevious

    fun updateCurrentlyReportedValues(projectId: Long, reportId: Long, currentlyReported: ReportCertificateCoFinancingColumn)

    fun updateAfterVerificationValues(projectId: Long, reportId: Long, afterVerification: ReportCertificateCoFinancingColumn)

}
