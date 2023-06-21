package io.cloudflight.jems.server.project.service.report.project.financialOverview.getReportUnitCostBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectReport
import io.cloudflight.jems.server.project.service.report.model.project.financialOverview.unitCost.CertificateUnitCostBreakdown
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportCertificateUnitCostBreakdown(
    private val reportCertificateUnitCostCalculatorService: GetReportCertificateUnitCostCalculatorService,
) : GetReportCertificateUnitCostsBreakdownInteractor {

    @CanRetrieveProjectReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportCertificateUnitCostBreakdownException::class)
    override fun get(projectId: Long, reportId: Long): CertificateUnitCostBreakdown =
        reportCertificateUnitCostCalculatorService.getSubmittedOrCalculateCurrent(projectId = projectId, reportId)

}
