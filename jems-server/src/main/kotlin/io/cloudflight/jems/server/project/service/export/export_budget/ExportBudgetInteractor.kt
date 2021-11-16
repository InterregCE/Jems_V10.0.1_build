package io.cloudflight.jems.server.project.service.export.export_budget

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult

interface ExportBudgetInteractor {
    fun exportDataToCsv(
        projectId: Long, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage, version: String? =  null
    ): ExportResult
}
