package io.cloudflight.jems.server.payments.service.audit.export.generatePaymentAuditExport

interface GeneratePaymentAuditExportInteractor {

    fun export(pluginKey: String, programmeFundId: Long?, accountingYearId: Long?)
}
