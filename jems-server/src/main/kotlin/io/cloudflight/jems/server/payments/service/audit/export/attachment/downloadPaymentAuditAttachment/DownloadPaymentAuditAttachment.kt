package io.cloudflight.jems.server.payments.service.audit.export.attachment.downloadPaymentAuditAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAudit
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPaymentAuditAttachment(
    private val filePersistence: JemsFilePersistence
) : DownloadPaymentAuditAttachmentInteractor {

    @CanRetrievePaymentsAudit
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPaymentAttachmentException::class)
    override fun download(fileId: Long): Pair<String, ByteArray> =
        filePersistence.downloadFile(JemsFileType.PaymentAuditAttachment, fileId)
            ?: throw FileNotFound()

}
