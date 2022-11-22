package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.getProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.server.project.service.report.ProjectReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.ProjectReportProcurementAttachmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.fillThisReportFlag
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurementAttachmentService(
    private val reportPersistence: ProjectReportPersistence,
    private val reportProcurementPersistence: ProjectReportProcurementPersistence,
    private val reportProcurementAttachmentPersistence: ProjectReportProcurementAttachmentPersistence,
) {

    @Transactional(readOnly = true)
    fun getAttachment(
        partnerId: Long,
        reportId: Long,
        procurementId: Long
    ): List<ProjectReportProcurementFile> {
        // we need to fetch those because of security to make sure those connections really exist
        val report = reportPersistence.getPartnerReportById(partnerId, reportId = reportId)
        val procurement = reportProcurementPersistence.getById(partnerId, procurementId = procurementId)

        return reportProcurementAttachmentPersistence
            .getAttachmentsBeforeAndIncludingReportId(procurement.id, reportId = report.id)
            .fillThisReportFlag(currentReportId = reportId)
    }

}
