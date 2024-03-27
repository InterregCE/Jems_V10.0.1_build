package io.cloudflight.jems.server.payments.service.account.attachment.uploadPaymentAccountAttachment

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsSystemFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.authorization.CanUpdatePaymentsAccount
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadPaymentAccountAttachment(
    private val filePersistence: JemsFilePersistence,
    private val fileRepository: JemsSystemFileService,
    private val securityService: SecurityService,
) : UploadPaymentAccountAttachmentInteractor {

    @CanUpdatePaymentsAccount
    @Transactional
    @ExceptionWrapper(UploadPaymentAccountAttachmentException::class)
    override fun upload(paymentAccountId: Long, file: ProjectFile): JemsFileMetadata {

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.PaymentAccountAttachment) {
            val location = generatePath(paymentAccountId)

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
