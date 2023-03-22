package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportLumpSumBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.lumpSum.CertificateLumpSumBreakdown
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportCertificateLumpSumBreakdown(
    private val reportCertificateLumpSumCalculatorService: GetReportCertificateLumpSumBreakdownCalculator,
) : GetReportCertificateLumpSumBreakdownInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportCertificateLumpSumBreakdownException::class)
    override fun get(projectId: Long, reportId: Long): CertificateLumpSumBreakdown =
        reportCertificateLumpSumCalculatorService.getSubmittedOrCalculateCurrent(projectId = projectId, reportId)

}
