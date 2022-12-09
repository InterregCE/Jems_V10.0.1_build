package io.cloudflight.jems.server.project.repository.report.partner.procurement.attachment

import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.ProjectPartnerReportProcurementAttachmentPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerReportProcurementAttachmentPersistenceProvider(
    private val reportProcurementAttachmentRepository: ProjectPartnerReportProcurementAttachmentRepository,
) : ProjectPartnerReportProcurementAttachmentPersistence {

    @Transactional(readOnly = true)
    override fun getAttachmentsBeforeAndIncludingReportId(procurementId: Long, reportId: Long) =
        reportProcurementAttachmentRepository
            .findTop30ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(procurementId, reportId = reportId)
            .toModel()

    @Transactional(readOnly = true)
    override fun countAttachmentsCreatedUpUntilNow(procurementId: Long, reportId: Long) =
        reportProcurementAttachmentRepository.countAttachmentsCreatedBeforeIncludingThis(procurementId = procurementId, reportId)

}
