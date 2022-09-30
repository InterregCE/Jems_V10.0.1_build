package io.cloudflight.jems.server.payments.service.attachment.uploadPaymentAttachment

import io.cloudflight.jems.server.authentication.service.SecurityService
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.PaymentPersistence
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.project.service.file.model.ProjectFile
import io.cloudflight.jems.server.project.service.file.uploadProjectFile.isFileTypeInvalid
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import io.cloudflight.jems.server.project.service.report.model.file.ProjectReportFileMetadata
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UploadPaymentAttachment(
    private val paymentPersistence: PaymentPersistence,
    private val reportFilePersistence: ProjectReportFilePersistence,
    private val securityService: SecurityService,
) : UploadPaymentAttachmentInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(UploadPaymentAttachmentException::class)
    override fun upload(paymentId: Long, file: ProjectFile): ProjectReportFileMetadata {
        if (!paymentPersistence.existsById(id = paymentId))
            throw PaymentNotFound()

        if (isFileTypeInvalid(file))
            throw FileTypeNotSupported()

        with(ProjectPartnerReportFileType.PaymentAttachment) {
            val location = generatePath(paymentId)

            if (reportFilePersistence.existsFile(exactPath = location, fileName = file.name))
                throw FileAlreadyExists()

            return reportFilePersistence.addAttachmentToPartnerReport(
                file = file.getFileMetadata(
                    null, null,
                    location = location,
                    type = this,
                    userId = securityService.getUserIdOrThrow(),
                )
            )
        }
    }

}
