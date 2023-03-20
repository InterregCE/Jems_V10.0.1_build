package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown

interface GetReportCertificateLumpSumsBreakdownInteractor {
    fun get(projectId: Long, reportId: Long): CertificateLumpSumBreakdown
}
