package io.cloudflight.jems.server.payments.service.regular.attachment.uploadPaymentAttachment

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.payments.service.regular.PaymentPersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadPaymentAttachment(
    private val paymentPersistence: PaymentPersistence,
    private val filePersistence: JemsFilePersistence,
    private val fileRepository: JemsProjectFileService,
    private val securityService: SecurityService,
) : UploadPaymentAttachmentInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(UploadPaymentAttachmentException::class)
    override fun upload(paymentId: Long, file: ProjectFile): JemsFileMetadata {
        val payment = paymentPersistence.getPaymentDetails(paymentId)

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.PaymentAttachment) {
            val location = generatePath(paymentId)

            if (filePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists()

            val fileToSave = file.getFileMetadata(
                projectId = payment.projectId,
                partnerId = null,
                location = location,
                type = this,
                userId = securityService.getUserIdOrThrow(),
            )

            return fileRepository.persistFile(fileToSave).toSimple()
        }
    }
}
