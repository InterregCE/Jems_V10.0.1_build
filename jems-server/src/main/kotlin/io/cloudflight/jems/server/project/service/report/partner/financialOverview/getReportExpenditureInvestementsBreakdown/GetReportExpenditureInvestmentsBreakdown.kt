package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureInvestementsBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportExpenditureInvestmentsBreakdown(
    private val calculator: GetReportExpenditureInvestmentsBreakdownCalculator,
) : GetReportExpenditureInvestmentsBreakdownInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportExpenditureInvestmentsBreakdownException::class)
    override fun get(partnerId: Long, reportId: Long) =
        calculator.get(partnerId = partnerId, reportId = reportId)

}
