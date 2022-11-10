package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableBudgetOptionsForReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerBudgetOptions
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableBudgetOptionsForReport(
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence
): GetAvailableBudgetOptionsForReportInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableBudgetOptionsForReportException::class)
    override fun getBudgetOptions(partnerId: Long, reportId: Long): ProjectPartnerBudgetOptions {
        return reportExpenditurePersistence.getAvailableBudgetOptions(partnerId, reportId)
    }
}
