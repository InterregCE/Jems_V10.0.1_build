package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown

import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown

interface GetReportCertificateLumpSumBreakdownInteractor {
    fun get(projectId: Long, reportId: Long): CertificateLumpSumBreakdown
}
