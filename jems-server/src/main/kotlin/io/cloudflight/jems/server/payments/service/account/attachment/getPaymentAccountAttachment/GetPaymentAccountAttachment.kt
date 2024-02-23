package io.cloudflight.jems.server.payments.service.account.attachment.getPaymentAccountAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAccountAttachment(
    private val filePersistence: JemsFilePersistence
) : GetPaymentAccountAttachmentInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentAccountAttachmentException::class)
    override fun list(paymentAccountId: Long, pageable: Pageable): Page<JemsFile> =
        filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = JemsFileType.Account.generatePath(paymentAccountId),
            filterSubtypes = emptySet(),
            filterUserIds = emptySet(),
        )

}
