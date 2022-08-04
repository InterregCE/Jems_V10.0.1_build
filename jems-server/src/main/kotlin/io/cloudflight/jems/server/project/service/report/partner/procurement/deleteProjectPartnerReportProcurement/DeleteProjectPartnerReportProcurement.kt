package io.cloudflight.jems.server.project.service.report.partner.procurement.deleteProjectPartnerReportProcurement

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.isClosed
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProjectPartnerReportProcurement(
    private val reportPersistence: ProjectReportPersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
) : DeleteProjectPartnerReportProcurementInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(DeleteProjectPartnerReportProcurementException::class)
    override fun delete(partnerId: Long, reportId: Long, procurementId: Long) {
        val report = reportPersistence.getPartnerReportStatusAndVersion(partnerId = partnerId, reportId)
        if (report.isClosed())
            throw ReportAlreadyClosed()

        reportProcurementPersistence.deletePartnerReportProcurement(
            partnerId = partnerId,
            reportId = reportId,
            procurementId = procurementId,
        )
    }

}
