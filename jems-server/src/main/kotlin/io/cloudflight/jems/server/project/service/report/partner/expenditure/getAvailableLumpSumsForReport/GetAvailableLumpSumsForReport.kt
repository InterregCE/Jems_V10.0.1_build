package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableLumpSumsForReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetAvailableLumpSumsForReport(
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
) : GetAvailableLumpSumsForReportInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableLumpSumsForReportException::class)
    override fun getLumpSums(partnerId: Long, reportId: Long): List<ProjectPartnerReportLumpSum> =
        reportExpenditurePersistence.getAvailableLumpSums(partnerId = partnerId, reportId = reportId)

}
