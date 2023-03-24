package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdownInteractor

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdown
interface GetReportCertificateInvestmentsBreakdownInteractor {
    fun get(projectId: Long, reportId: Long): CertificateInvestmentBreakdown
}
