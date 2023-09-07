package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getDeductionOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationFinance
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.deductionOverview.CertificateVerificationDeductionOverview
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportVerificationDeductionOverview(
    private val deductionByTypologyOfErrorCalculator: ProjectReportVerificationDeductionOverviewCalculator
): GetProjectReportVerificationDeductionOverviewInteractor {

    @CanViewReportVerificationFinance
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportVerificationDeductionOverviewException::class)
    override fun getDeductionOverview(reportId: Long): List<CertificateVerificationDeductionOverview> =
        deductionByTypologyOfErrorCalculator.getDeductionOverview(reportId)
}