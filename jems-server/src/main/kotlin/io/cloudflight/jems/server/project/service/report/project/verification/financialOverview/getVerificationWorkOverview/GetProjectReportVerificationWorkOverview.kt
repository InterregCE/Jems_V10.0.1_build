package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getVerificationWorkOverview

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationFinance
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.workOverview.VerificationWorkOverview
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportVerificationWorkOverview(
    private val calculator: GetProjectReportVerificationWorkOverviewCalculator,
) : GetProjectReportVerificationWorkOverviewInteractor {

    @CanViewReportVerificationFinance
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportVerificationWorkOverviewException::class)
    override fun get(reportId: Long): VerificationWorkOverview =
        calculator.getWorkOverviewPerPartner(reportId)

}
