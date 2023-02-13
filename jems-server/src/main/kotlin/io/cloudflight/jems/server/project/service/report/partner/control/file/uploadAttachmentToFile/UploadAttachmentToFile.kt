package io.cloudflight.jems.server.project.service.report.partner.control.file.uploadAttachmentToFile

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReport
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import io.cloudflight.jems.server.project.service.report.partner.contribution.uploadFileToProjectPartnerReportContribution.FileTypeNotSupported
import io.cloudflight.jems.server.project.service.report.partner.control.file.ProjectPartnerReportControlFilePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadAttachmentToFile(
    private val projectPartnerReportControlFilePersistence: ProjectPartnerReportControlFilePersistence,
    private val partnerPersistence: PartnerPersistence,
    private val securityService: SecurityService,
) : UploadAttachmentToFileInteractor {

    @CanViewPartnerControlReport
    @Transactional
    @ExceptionWrapper(UploadAttachmentToFileException::class)
    override fun uploadAttachment(
        partnerId: Long,
        reportId: Long,
        fileId: Long,
        file: ProjectFile
    ): JemsFileMetadata {
        if (!projectPartnerReportControlFilePersistence.existsByFileId(fileId))
            throw ControlFileNotFoundException(fileId = fileId)

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.ControlDocument) {
            val projectId = partnerPersistence.getProjectIdForPartnerId(partnerId)
            val location = generatePath(projectId, partnerId, reportId)

            return projectPartnerReportControlFilePersistence.updateCertificateAttachment(
                fileId = fileId,
                file = file.getFileMetadata(projectId, partnerId, location, type = this, securityService.getUserIdOrThrow()),
            )
        }
    }
}
