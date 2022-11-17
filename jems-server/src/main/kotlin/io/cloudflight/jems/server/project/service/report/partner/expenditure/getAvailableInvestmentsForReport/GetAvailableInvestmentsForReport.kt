package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableInvestmentsForReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportInvestment
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import io.cloudflight.jems.server.project.service.workpackage.model.InvestmentSummary
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableInvestmentsForReport(
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence
) : GetAvailableInvestmentsForReportInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableInvestmentsForReportException::class)
    override fun getInvestments(partnerId: Long, reportId: Long): List<ProjectPartnerReportInvestment> {
        return reportExpenditurePersistence.getAvailableInvestments(partnerId, reportId)
    }
}
