package io.cloudflight.jems.server.project.service.report.partner.expenditure.deleteParkedExpenditure

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.ReportStatus
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.control.expenditure.PartnerReportParkedExpenditurePersistence
import io.cloudflight.jems.server.project.service.report.partner.partnerReportExpenditureDeleted
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteParkedExpenditure(
    private val reportParkedExpenditurePersistence: PartnerReportParkedExpenditurePersistence,
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val auditPublisher: ApplicationEventPublisher,
) : DeleteParkedExpenditureInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(DeleteParkedExpenditureException::class)
    override fun deleteParkedExpenditure(partnerId: Long, reportId: Long, expenditureId: Long) {
        val report = reportPersistence.getPartnerReportById(partnerId = partnerId, reportId = reportId)
        if (!report.status.isOpenForNumbersChanges())
            throw DeletingParkedForbiddenIfReOpenedReportIsNotLast()

        val parked = reportParkedExpenditurePersistence.getParkedExpendituresByIdForPartner(partnerId)
        if (expenditureId !in parked.keys)
            throw ParkedExpenditureNotFound(expenditureId)

        reportParkedExpenditurePersistence.unParkExpenditures(setOf(expenditureId))

        auditPublisher.publishEvent(
            partnerReportExpenditureDeleted(
                context = this,
                projectId = partnerPersistence.getProjectIdForPartnerId(id = partnerId, report.version),
                partnerReport = report,
                expenditure = parked[expenditureId]!!,
            )
        )
    }

}
