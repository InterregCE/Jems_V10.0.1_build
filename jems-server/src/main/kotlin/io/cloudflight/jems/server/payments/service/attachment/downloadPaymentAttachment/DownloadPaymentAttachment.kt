package io.cloudflight.jems.server.payments.service.attachment.downloadPaymentAttachment

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePayments
import io.cloudflight.jems.server.project.service.report.file.ProjectReportFilePersistence
import io.cloudflight.jems.server.project.service.report.model.file.ProjectPartnerReportFileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPaymentAttachment(
    private val reportFilePersistence: ProjectReportFilePersistence,
) : DownloadPaymentAttachmentInteractor {

    @CanRetrievePayments
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPaymentAttachmentException::class)
    override fun download(fileId: Long): Pair<String, ByteArray> =
        reportFilePersistence.downloadFile(ProjectPartnerReportFileType.PaymentAttachment, fileId)
            ?: throw FileNotFound()

}
