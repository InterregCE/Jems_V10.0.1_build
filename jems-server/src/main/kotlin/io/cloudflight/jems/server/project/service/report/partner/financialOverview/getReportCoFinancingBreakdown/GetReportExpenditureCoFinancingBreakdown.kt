package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportCoFinancingBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportExpenditureCoFinancingBreakdown(
    private val calculator: GetReportExpenditureCoFinancingBreakdownCalculator,
) : GetReportExpenditureCoFinancingBreakdownInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportExpenditureCoFinancingBreakdownException::class)
    override fun get(partnerId: Long, reportId: Long) =
        calculator.get(partnerId = partnerId, reportId = reportId)

}
