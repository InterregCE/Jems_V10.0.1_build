package io.cloudflight.jems.server.payments.service.regular.attachment.getPaymentAttchament

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePayments
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType.PaymentAttachment
import io.cloudflight.jems.server.project.service.report.model.file.JemsFile
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAttachment(
    private val reportFilePersistence: ProjectReportFilePersistence,
) : GetPaymentAttachmentInteractor {

    @CanRetrievePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentAttachmentException::class)
    override fun list(paymentId: Long, pageable: Pageable): Page<JemsFile> =
        reportFilePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = PaymentAttachment.generatePath(paymentId),
            filterSubtypes = emptySet(),
            filterUserIds = emptySet(),
        )

}
