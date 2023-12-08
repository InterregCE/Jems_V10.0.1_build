package io.cloudflight.jems.server.payments.service.audit.export.downloadPaymentApplicationToEcAuditExport

import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.common.file.service.JemsFilePersistence
import io.cloudflight.jems.server.common.file.service.model.JemsFileType
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAudit
import io.cloudflight.jems.server.payments.service.audit.export.PaymentApplicationToEcAuditExportPersistence
import io.cloudflight.jems.server.payments.service.regular.attachment.downloadPaymentAttachment.FileNotFound
import io.cloudflight.jems.server.project.authorization.CanViewPartnerControlReportFile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPaymentApplicationToEcAuditExportFile(
    private val filePersistence: JemsFilePersistence,
    private val paymentApplicationToEcAuditExportPersistence: PaymentApplicationToEcAuditExportPersistence,
) : DownloadPaymentApplicationToEcAuditExportInteractor {

    @CanRetrievePaymentsAudit
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPaymentApplicationToEcAuditExportException::class)
    override fun download(fileId: Long): Pair<String, ByteArray> {
        val auditExport = paymentApplicationToEcAuditExportPersistence.getById(fileId)
        return filePersistence.downloadFile(JemsFileType.PaymentToEcAuditExport, auditExport.generatedFile.id) ?: throw FileNotFound()
    }
}
