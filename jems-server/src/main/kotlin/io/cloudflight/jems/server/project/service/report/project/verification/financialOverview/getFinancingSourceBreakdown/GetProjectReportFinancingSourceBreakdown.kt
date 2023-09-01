package io.cloudflight.jems.server.project.service.report.project.verification.financialOverview.getFinancingSourceBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewReportVerificationFinance
import io.cloudflight.jems.server.project.service.report.model.project.verification.financialOverview.financingSource.FinancingSourceBreakdown
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectReportFinancingSourceBreakdown(
    private val calculator: GetProjectReportFinancingSourceBreakdownCalculator,
) : GetProjectReportFinancingSourceBreakdownInteractor {

    @CanViewReportVerificationFinance
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectReportFinancingSourceBreakdownException::class)
    override fun get(projectId: Long, reportId: Long): FinancingSourceBreakdown =
        calculator.getFinancingSource(projectId = projectId, reportId = reportId)

}
