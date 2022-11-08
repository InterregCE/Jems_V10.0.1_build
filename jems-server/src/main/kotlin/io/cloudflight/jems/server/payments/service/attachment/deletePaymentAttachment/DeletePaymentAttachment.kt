package io.cloudflight.jems.server.payments.service.attachment.deletePaymentAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.minio.GenericPaymentFileRepository
import io.cloudflight.jems.server.payments.authorization.CanUpdatePayments
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType.PaymentAttachment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePaymentAttachment(
    private val genericFileRepository: GenericPaymentFileRepository,
) : DeletePaymentAttachmentInteractor {

    @CanUpdatePayments
    @Transactional
    @ExceptionWrapper(DeletePaymentAttachmentException::class)
    override fun delete(fileId: Long) {
        genericFileRepository.delete(PaymentAttachment, fileId = fileId)
    }

}
