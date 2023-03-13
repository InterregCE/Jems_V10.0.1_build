package io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.uploadFileToProjectPartnerReportProcurementAttachment

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.project.authorization.CanEditPartnerReport
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.ProjectPartnerReportPersistence
import io.cloudflight.jems.server.project.service.report.partner.file.ProjectPartnerReportFilePersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.ProjectPartnerReportProcurementPersistence
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.MAX_AMOUNT_OF_ATTACHMENT
import io.cloudflight.jems.server.project.service.report.partner.procurement.attachment.ProjectPartnerReportProcurementAttachmentPersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadFileToProjectPartnerReportProcurementAttachment(
    private val reportPersistence: ProjectPartnerReportPersistence,
    private val reportProcurementPersistence: ProjectPartnerReportProcurementPersistence,
    private val partnerPersistence: PartnerPersistence,
    private val reportFilePersistence: ProjectPartnerReportFilePersistence,
    private val filePersistence: JemsFilePersistence,
    private val reportProcurementAttachmentPersistence: ProjectPartnerReportProcurementAttachmentPersistence,
    private val securityService: SecurityService,
) : UploadFileToProjectPartnerReportProcurementAttachmentInteractor {

    @CanEditPartnerReport
    @Transactional
    @ExceptionWrapper(UploadFileToProjectPartnerReportProcurementAttachmentException::class)
    override fun uploadToProcurement(
        partnerId: Long,
        reportId: Long,
        procurementId: Long,
        file: ProjectFile,
    ): JemsFileMetadata {
        // we need to fetch those 2 because of security
        val procurement = reportProcurementPersistence.getById(partnerId, procurementId)
        if (!reportPersistence.exists(partnerId, reportId = reportId))
            throw ReportNotFound(reportId)

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        validateMaxAmountOfAttachments(procurementId = procurement.id, reportId)

        with(JemsFileType.ProcurementAttachment) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId, procurement.id)

            if (filePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists(file.name)

            return reportFilePersistence.addPartnerReportProcurementAttachment(
                reportId = reportId,
                procurementId = procurement.id,
                file = file.getFileMetadata(projectId, partnerId, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }

    private fun validateMaxAmountOfAttachments(procurementId: Long, reportId: Long) {
        val amountBeforeSave = reportProcurementAttachmentPersistence
            .countAttachmentsCreatedUpUntilNow(procurementId, reportId)
        if (amountBeforeSave >= MAX_AMOUNT_OF_ATTACHMENT)
            throw MaxAmountOfAttachmentReachedException(MAX_AMOUNT_OF_ATTACHMENT)
    }
}
