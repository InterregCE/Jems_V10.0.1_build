package io.cloudflight.jems.server.payments.service.attachment.deletePaymentAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.PaymentAttachment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentAttachment(
    private val reportFilePersistence: ProjectReportFilePersistence,
) : DeletePaymentAttachmentInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(DeletePaymentAttachmentException::class)
    override fun delete(fileId: Long) {
        if (!reportFilePersistence.existsFile(type = PaymentAttachment, fileId = fileId))
            throw FileNotFound()

        reportFilePersistence.deleteFile(PaymentAttachment, fileId = fileId)
    }

}
