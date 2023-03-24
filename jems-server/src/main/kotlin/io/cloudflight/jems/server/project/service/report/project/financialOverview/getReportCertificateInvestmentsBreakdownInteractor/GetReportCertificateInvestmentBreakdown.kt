package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCertificateInvestmentsBreakdownInteractor

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.investment.CertificateInvestmentBreakdown
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportCertificateInvestmentBreakdown(
    private val reportCertificateInvestmentCalculatorService: GetReportCertificateInvestmentCalculatorService,
) : GetReportCertificateInvestmentsBreakdownInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportCertificateInvestmentBreakdownException::class)
    override fun get(projectId: Long, reportId: Long): CertificateInvestmentBreakdown =
        reportCertificateInvestmentCalculatorService.getSubmittedOrCalculateCurrent(projectId = projectId, reportId)

}
