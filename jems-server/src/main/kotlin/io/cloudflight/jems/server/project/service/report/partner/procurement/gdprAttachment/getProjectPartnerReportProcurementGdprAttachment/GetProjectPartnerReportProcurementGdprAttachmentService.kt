package io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.getProjectPartnerReportProcurementGdprAttachment

import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.model.partner.procurement.ProjectReportProcurementFile
import io.cloudflight.jems.server.project.service.report.partner.SensitiveDataAuthorizationService
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.fillThisReportFlag
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.ProjectPartnerReportProcurementGdprAttachmentPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.anonymizeSensitiveData
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProjectPartnerReportProcurementGdprAttachmentService(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportProcurementPersistence: ProjectPartnerReportProcurementPersistence,
    private val reportProcurementGdprAttachmentPersistence: ProjectPartnerReportProcurementGdprAttachmentPersistence,
    private val sensitiveDataAuthorizationService: SensitiveDataAuthorizationService,
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
        val hasUserAccessToGdprData = this.sensitiveDataAuthorizationService.canViewPartnerSensitiveData(partnerId)

        return reportProcurementGdprAttachmentPersistence
            .getGdprAttachmentsBeforeAndIncludingReportId(procurement.id, reportId = report.id)
            .fillThisReportFlag(currentReportId = reportId).apply {
                if(!hasUserAccessToGdprData) {
                    this.anonymizeSensitiveData()
                }
            }
    }
}
