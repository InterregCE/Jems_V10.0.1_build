package io.cloudflight.jems.server.project.service.budget.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.budget_export.ExportResult

interface ExportBudgetInteractor {
    fun exportDataToCsv(projectId: Long, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage, version: String?): ExportResult
}
