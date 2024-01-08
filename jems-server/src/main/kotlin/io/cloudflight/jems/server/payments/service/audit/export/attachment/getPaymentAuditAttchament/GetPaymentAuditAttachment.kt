package io.cloudflight.jems.server.payments.service.audit.export.attachment.getPaymentAuditAttchament

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFile
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAudit
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPaymentAuditAttachment(
    private val filePersistence: JemsFilePersistence
) : GetPaymentAuditAttachmentInteractor {

    @CanRetrievePaymentsAudit
    @Transactional(readOnly = true)
    @ExceptionWrapper(GetPaymentAuditAttachmentException::class)
    override fun list(pageable: Pageable): Page<JemsFile> {
        val filePathPrefix = JemsFileType.PaymentAuditAttachment.generatePath()

        return filePersistence.listAttachments(
            pageable = pageable,
            indexPrefix = filePathPrefix,
            filterSubtypes = emptySet(),
            filterUserIds = emptySet(),
        )
    }

}
