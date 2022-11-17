package io.cloudflight.jems.server.project.service.report.partner.expenditure.getAvailableLumpSumsForReport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerReport
import io.cloudflight.jems.server.project.service.report.model.partner.expenditure.ProjectPartnerReportLumpSum
import io.cloudflight.jems.server.project.service.report.partner.expenditure.ProjectReportExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class GetAvailableLumpSumsForReport(
    private val reportExpenditurePersistence: ProjectReportExpenditurePersistence,
) : GetAvailableLumpSumsForReportInteractor {

    @CanViewPartnerReport
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetAvailableLumpSumsForReportException::class)
    override fun getLumpSums(partnerId: Long, reportId: Long): List<ProjectPartnerReportLumpSum> =
        reportExpenditurePersistence.getAvailableLumpSums(partnerId = partnerId, reportId = reportId)
            .onlyNonFastTrack()
            .onlyNonZero()

    private fun List<ProjectPartnerReportLumpSum>.onlyNonFastTrack() = filter { !it.fastTrack }

    private fun List<ProjectPartnerReportLumpSum>.onlyNonZero() = filter { it.cost.compareTo(BigDecimal.ZERO) != 0 }

}
