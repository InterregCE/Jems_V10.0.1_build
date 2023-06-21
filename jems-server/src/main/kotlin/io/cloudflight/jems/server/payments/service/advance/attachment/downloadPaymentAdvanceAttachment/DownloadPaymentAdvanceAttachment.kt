package io.cloudflight.jems.server.payments.service.advance.attachment.downloadPaymentAdvanceAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPaymentAdvanceAttachment(
    private val filePersistence: JemsFilePersistence
): DownloadPaymentAdvAttachmentInteractor {

    @CanRetrieveAdvancePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPaymentAdvAttachmentException::class)
    override fun download(fileId: Long): Pair<String, ByteArray> =
        filePersistence.downloadFile(JemsFileType.PaymentAdvanceAttachment, fileId) ?: throw FileNotFound()
}
