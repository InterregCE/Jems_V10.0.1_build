package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportCertificateCoFinancingBreakdown(
    private val calculator: GetReportCertificateCoFinancingBreakdownCalculator,
) : GetReportCertificateCoFinancingBreakdownInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportCertificateCoFinancingBreakdownException::class)
    override fun get(projectId: Long, reportId: Long) =
        calculator.get(projectId = projectId, reportId = reportId)
}
