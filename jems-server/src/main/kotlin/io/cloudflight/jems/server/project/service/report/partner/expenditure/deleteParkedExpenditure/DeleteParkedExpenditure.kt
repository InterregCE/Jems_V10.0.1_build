package io.cloudflight.jems.server.project.service.report.partner.expenditure.deleteParkedExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReportNotSpecific
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteParkedExpenditure(
    private val reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence,
) : DeleteParkedExpenditureInteractor {

    @CanEditPartnerReportNotSpecific
    @Transactional
    @ExceptionWrapper(DeleteParkedExpenditureException::class)
    override fun deleteParkedExpenditure(partnerId: Long, expenditureId: Long) {
        val parkedIds = reportParkedExpenditurePersistence.getParkedExpendituresByIdForPartner(partnerId, ReportStatus.Certified).keys
        if (expenditureId in parkedIds) {
            reportParkedExpenditurePersistence.unParkExpenditures(setOf(expenditureId))
        }
    }

}
