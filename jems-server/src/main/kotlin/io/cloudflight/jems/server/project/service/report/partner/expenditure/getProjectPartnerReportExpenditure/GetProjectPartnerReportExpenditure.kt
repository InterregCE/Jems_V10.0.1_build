package io.cloudflight.jems.server.project.service.report.partner.expenditure.getProjectPartnerReportExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportExpenditure(
    private val calculator: GetProjectPartnerReportExpenditureCalculator,
) : GetProjectPartnerReportExpenditureInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetProjectPartnerReportExpenditureException::class)
    override fun getExpenditureCosts(partnerId: Long, reportId: Long) =
        calculator.getExpenditureCosts(partnerId = partnerId, reportId = reportId)

}
