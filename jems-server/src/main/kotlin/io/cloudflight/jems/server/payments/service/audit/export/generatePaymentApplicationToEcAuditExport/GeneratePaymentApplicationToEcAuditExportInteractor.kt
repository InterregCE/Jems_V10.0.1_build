package io.cloudflight.jems.server.payments.service.audit.export.generatePaymentApplicationToEcAuditExport

interface GeneratePaymentApplicationToEcAuditExportInteractor {

    fun export(pluginKey: String, programmeFundId: Long?, accountingYearId: Long?)

}
