package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureUnitCostBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportExpenditureUnitCostBreakdown(
    private val calculator: GetReportExpenditureUnitCostBreakdownCalculator,
) : GetReportExpenditureUnitCostBreakdownInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportExpenditureUnitCostBreakdownException::class)
    override fun get(partnerId: Long, reportId: Long) =
        calculator.get(partnerId = partnerId, reportId = reportId)

}
