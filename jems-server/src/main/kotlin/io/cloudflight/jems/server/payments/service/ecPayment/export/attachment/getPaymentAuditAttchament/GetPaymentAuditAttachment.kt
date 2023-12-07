package io.cloudflight.jems.server.payments.service.ecPayment.export.attachment.getPaymentAuditAttchament

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.payments.authorization.CanRetrievePayments
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAuditAttachment(
    private val filePersistence: JemsFilePersistence
) : GetPaymentAuditAttachmentInteractor {

    @CanRetrievePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentAuditAttachmentException::class)
    override fun list(pageable: Pageable): Page<JemsFile> =
        filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = "Payment/Audit/PaymentAuditAttachment/",
            filterSubtypes = emptySet(),
            filterUserIds = emptySet(),
        )

}
