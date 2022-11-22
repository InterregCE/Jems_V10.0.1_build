package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureLumpSumBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportExpenditureLumpSumBreakdown(
    private val calculator: GetReportExpenditureLumpSumBreakdownCalculator,
) : GetReportExpenditureLumpSumBreakdownInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportExpenditureLumpSumBreakdownException::class)
    override fun get(partnerId: Long, reportId: Long) =
        calculator.get(partnerId = partnerId, reportId = reportId)

}
