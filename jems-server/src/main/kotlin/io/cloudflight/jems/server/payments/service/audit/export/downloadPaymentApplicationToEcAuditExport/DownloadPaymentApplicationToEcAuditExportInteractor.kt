package io.cloudflight.jems.server.payments.service.audit.export.downloadPaymentApplicationToEcAuditExport

interface DownloadPaymentApplicationToEcAuditExportInteractor {
    fun download(fileId: Long): Pair<String, ByteArray>
}
