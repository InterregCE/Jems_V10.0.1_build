package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.CertificateCoFinancingBreakdown

interface GetReportCertificateCoFinancingBreakdownInteractor {
    fun get(projectId: Long, reportId: Long): CertificateCoFinancingBreakdown
}
