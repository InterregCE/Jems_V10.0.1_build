package io.cloudflight.jems.server.payments.service.ecPayment.attachment.downloadPaymentToEcAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentApplicationsToEc
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPaymentToEcAttachment(
    private val filePersistence: JemsFilePersistence
) : DownloadPaymentToEcAttachmentInteractor {

    @CanRetrievePaymentApplicationsToEc
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPaymentToEcAttachmentException::class)
    override fun download(fileId: Long): Pair<String, ByteArray> =
        filePersistence.downloadFile(JemsFileType.PaymentToEcAttachment, fileId)
            ?: throw FileNotFound()

}
