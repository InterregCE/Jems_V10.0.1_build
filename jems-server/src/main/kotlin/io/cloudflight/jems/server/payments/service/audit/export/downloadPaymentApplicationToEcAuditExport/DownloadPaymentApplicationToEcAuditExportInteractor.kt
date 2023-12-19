package io.cloudflight.jems.server.payments.service.audit.export.downloadPaymentApplicationToEcAuditExport

import io.cloudflight.jems.plugin.contract.export.ExportResult

interface DownloadPaymentApplicationToEcAuditExportInteractor {

    fun download(fileId: Long, pluginKey: String): ExportResult

}
