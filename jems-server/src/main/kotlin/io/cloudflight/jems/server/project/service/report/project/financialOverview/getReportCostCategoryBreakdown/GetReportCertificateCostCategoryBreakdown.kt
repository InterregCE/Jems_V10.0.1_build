package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportCostCategoryBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.costCategory.CertificateCostCategoryBreakdown
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportCertificateCostCategoryBreakdown(
    private val reportCertificateCostCategoryCalculatorService: GetReportCertificateCostCategoryBreakdownCalculator,
) : GetReportCertificateCostCategoryBreakdownInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportCertificateCostCategoryBreakdownException::class)
    override fun get(projectId: Long, reportId: Long): CertificateCostCategoryBreakdown =
        reportCertificateCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(projectId = projectId, reportId)

}
