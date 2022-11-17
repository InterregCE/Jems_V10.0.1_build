package io.cloudflight.jems.server.payments.service.advance.attachment.downloadPaymentAdvanceAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrieveAdvancePayments
import io.cloudflight.jems.server.project.service.report.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.JemsFileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPaymentAdvanceAttachment(
    private val reportFilePersistence: ProjectReportFilePersistence,
): DownloadPaymentAdvAttachmentInteractor {

    @CanRetrieveAdvancePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPaymentAdvAttachmentException::class)
    override fun download(fileId: Long): Pair<String, ByteArray> =
        reportFilePersistence.downloadFile(JemsFileType.PaymentAdvanceAttachment, fileId) ?: throw FileNotFound()
}