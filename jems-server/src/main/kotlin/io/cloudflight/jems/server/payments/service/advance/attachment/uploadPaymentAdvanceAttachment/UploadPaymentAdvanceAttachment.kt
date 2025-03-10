package io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.JemsProjectFileService
import io.cloudflight.jems.server.common.file.service.model.JemsFileMetadata
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanUpdateAdvancePayments
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadPaymentAdvanceAttachment(
    private val paymentPersistence: PaymentAdvancePersistence,
    private val filePersistence: JemsFilePersistence,
    private val fileRepository: JemsProjectFileService,
    private val securityService: SecurityService,
) : UploadPaymentAdvAttachmentInteractor {

    @CanUpdateAdvancePayments
    @Transactional
    @ExceptionWrapper(UploadPaymentAdvAttachmentException::class)
    override fun upload(paymentId: Long, file: ProjectFile): JemsFileMetadata {
        val payment = paymentPersistence.getPaymentDetail(paymentId)

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.PaymentAdvanceAttachment) {
            val location = generatePath(paymentId)

            if (filePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists(file.name)

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
