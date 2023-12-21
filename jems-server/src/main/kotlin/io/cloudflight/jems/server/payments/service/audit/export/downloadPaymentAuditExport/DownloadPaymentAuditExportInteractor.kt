package io.cloudflight.jems.server.payments.service.audit.export.downloadPaymentAuditExport

import io.cloudflight.jems.plugin.contract.export.ExportResult

interface DownloadPaymentAuditExportInteractor {

    fun download(fileId: Long, pluginKey: String): ExportResult
}
