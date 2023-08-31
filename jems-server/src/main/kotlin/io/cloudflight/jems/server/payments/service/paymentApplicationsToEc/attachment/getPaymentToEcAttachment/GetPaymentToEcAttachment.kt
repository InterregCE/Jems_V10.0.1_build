package io.cloudflight.jems.server.payments.service.paymentApplicationsToEc.attachment.getPaymentToEcAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentToEcAttachment(
    private val filePersistence: JemsFilePersistence
) : GetPaymentToEcAttachmentInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentToEcAttachmentException::class)
    override fun list(paymentToEcId: Long, pageable: Pageable): Page<JemsFile> =
        filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = JemsFileType.PaymentToEcAttachment.generatePath(paymentToEcId),
            filterSubtypes = emptySet(),
            filterUserIds = emptySet(),
        )

}
