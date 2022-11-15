package io.cloudflight.jems.server.payments.service.attachment.uploadPaymentAttachment

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.minio.GenericProjectFileRepository
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.partner.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.partner.file.ProjectReportFileMetadata
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadPaymentAttachment(
    private val paymentPersistence: PaymentPersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val genericFileRepository: GenericProjectFileRepository,
    private val securityService: SecurityService,
) : UploadPaymentAttachmentInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(UploadPaymentAttachmentException::class)
    override fun upload(paymentId: Long, file: ProjectFile): ProjectReportFileMetadata {
        val payment = paymentPersistence.getPaymentDetails(paymentId)

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(ProjectPartnerReportFileType.PaymentAttachment) {
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

            return genericFileRepository.persistProjectFile(fileToSave)
        }
    }

}
