package io.cloudflight.jems.server.payments.service.account.attachment.downloadPaymentAccountAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAccount
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPaymentAccountAttachment(
    private val filePersistence: JemsFilePersistence
) : DownloadPaymentAccountAttachmentInteractor {

    @CanRetrievePaymentsAccount
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPaymentAccountAttachmentException::class)
    override fun download(fileId: Long): Pair<String, ByteArray> =
        filePersistence.downloadFile(JemsFileType.PaymentAccountAttachment, fileId)
            ?: throw FileNotFound()

}
