package io.cloudflight.jems.server.payments.service.advance.attachment.uploadPaymentAdvanceAttachment

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.minio.JemsProjectFileRepository
import io.cloudflight.jems.server.payments.authorization.CanUpdateAdvancePayments
import io.cloudflight.jems.server.payments.service.advance.PaymentAdvancePersistence
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileMetadata
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadPaymentAdvanceAttachment(
    private val paymentPersistence: PaymentAdvancePersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val fileRepository: JemsProjectFileRepository,
    private val securityService: SecurityService,
): UploadPaymentAdvAttachmentInteractor {

    @CanUpdateAdvancePayments
    @Transactional
    @ExceptionWrapper(UploadPaymentAdvAttachmentException::class)
    override fun upload(paymentId: Long, file: ProjectFile): JemsFileMetadata {
        val payment = paymentPersistence.getPaymentDetail(paymentId)

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(JemsFileType.PaymentAdvanceAttachment) {
            val location = generatePath(paymentId)

            if (reportFilePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists()

            val fileToSave = file.getFileMetadata(
                projectId = payment.projectId,
                partnerId = null,
                location = location,
                type = this,
                userId = securityService.getUserIdOrThrow(),
            )

            return fileRepository.persistProjectFile(fileToSave)
        }
    }
}