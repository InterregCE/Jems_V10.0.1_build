package io.cloudflight.jems.server.project.service.report.partner.financialOverview.getReportExpenditureBreakdown

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.financialOverview.costCategory.ExpenditureCostCategoryBreakdown
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetReportExpenditureCostCategoryBreakdown(
    private val reportExpenditureCostCategoryCalculatorService: GetReportExpenditureCostCategoryCalculatorService,
) : GetReportExpenditureCostCategoryBreakdownInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetReportExpenditureCostCategoryBreakdownException::class)
    override fun get(partnerId: Long, reportId: Long): ExpenditureCostCategoryBreakdown =
        reportExpenditureCostCategoryCalculatorService.getSubmittedOrCalculateCurrent(partnerId = partnerId, reportId)

}
