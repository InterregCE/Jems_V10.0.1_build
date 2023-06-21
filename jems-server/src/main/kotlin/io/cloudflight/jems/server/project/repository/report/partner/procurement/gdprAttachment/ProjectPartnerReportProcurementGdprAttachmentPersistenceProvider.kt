package io.cloudflight.jems.server.project.repository.report.partner.procurement.gdprAttachment

import io.cloudflight.jems.server.project.service.report.partner.procurement.gdprAttachment.ProjectPartnerReportProcurementGdprAttachmentPersistence
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class ProjectPartnerReportProcurementGdprAttachmentPersistenceProvider(
    private val reportProcurementGdprAttachmentRepository: ProjectPartnerReportProcurementGdprAttachmentRepository,
) : ProjectPartnerReportProcurementGdprAttachmentPersistence {

    @Transactional(readOnly = true)
    override fun getGdprAttachmentsBeforeAndIncludingReportId(procurementId: Long, reportId: Long) =
        reportProcurementGdprAttachmentRepository
            .findTop30ByProcurementIdAndCreatedInReportIdLessThanEqualOrderByCreatedInReportIdAscIdAsc(procurementId, reportId = reportId)
            .toModel()

    @Transactional(readOnly = true)
    override fun countGdprAttachmentsCreatedUpUntilNow(procurementId: Long, reportId: Long) =
        reportProcurementGdprAttachmentRepository.countAttachmentsCreatedBeforeIncludingThis(procurementId = procurementId, reportId)

}
