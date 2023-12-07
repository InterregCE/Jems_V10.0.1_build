package io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.uploadPaymentAuditAttachment

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadPaymentAuditAttachment(
    private val filePersistence: JemsFilePersistence,
    private val fileRepository: JemsSystemFileService,
    private val securityService: SecurityService,
) : UploadPaymentAuditAttachmentInteractor {

    @CanUpdatePayments //TODO UPDATE WITH PROPER AUTHORIZATION
    @Transactional
    @ExceptionWrapper(UploadPaymentAttachmentException::class)
    override fun upload(file: ProjectFile): JemsFileMetadata {

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.PaymentAuditAttachment) {
            val location = "Payment/Audit/PaymentAuditAttachment/"

            if (filePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists()

            val fileToSave = file.getFileMetadata(
                projectId = null,
                partnerId = null,
                location = location,
                type = this,
                userId = securityService.getUserIdOrThrow(),
            )

            return fileRepository.persistFile(fileToSave).toSimple()
        }
    }
}
