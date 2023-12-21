package io.cloudflight.jems.server.payments.service.audit.export.downloadPaymentAuditExport

import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.payments.authorization.CanRetrievePaymentsAudit
import io.cloudflight.jems.server.payments.model.ec.export.PaymentToEcExportMetadata
import io.cloudflight.jems.server.payments.service.audit.export.PaymentAuditExportPersistence
import io.cloudflight.jems.server.programme.service.downloadProgrammeDataExportFile.ExportFileIsNotReadyException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DownloadPaymentAuditExport(
    private val paymentAuditExportPersistence: PaymentAuditExportPersistence,
) : DownloadPaymentAuditExportInteractor {

    @CanRetrievePaymentsAudit
    @Transactional(readOnly = true)
    @ExceptionWrapper(DownloadPaymentAuditExportException::class)
    override fun download(fileId: Long, pluginKey: String): ExportResult {
        val auditExport = paymentAuditExportPersistence.getById(fileId)

        return paymentAuditExportPersistence.getById(auditExport.id).let { metadata ->
            throwIfFileIsNotReady(metadata)
            ExportResult(
                metadata.contentType!!,
                metadata.fileName!!,
                paymentAuditExportPersistence.getExportFile(
                    pluginKey,
                    auditExport.fund?.type,
                    auditExport.accountingYear?.year
                ),
                startTime = metadata.exportStartedAt,
                endTime = metadata.exportEndedAt
            )
        }
    }

    private fun throwIfFileIsNotReady(metadata: PaymentToEcExportMetadata) {
        if (metadata.fileName.isNullOrBlank() || metadata.contentType.isNullOrBlank() || metadata.exportEndedAt == null)
            throw ExportFileIsNotReadyException()
    }
}
