package io.cloudflight.jems.server.payments.service.audit.export.generatePaymentApplicationToEcAuditExport

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundTypeDTO

interface GeneratePaymentApplicationToEcAuditExportInteractor {
    fun export(pluginKey: String, accountingYear: Short?, programmeFundType: ProgrammeFundTypeDTO?)
}
