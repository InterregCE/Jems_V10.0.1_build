package io.cloudflight.jems.server.project.service.export.export_budget

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.BudgetExportPlugin
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import org.springframework.stereotype.Service

@Service
class ExportBudget(private val jemsPluginRegistry: JemsPluginRegistry) : ExportBudgetInteractor {


    @ExceptionWrapper(ExportBudgetException::class)
    @CanRetrieveProjectForm
    override fun exportDataToCsv(
        projectId: Long, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage, version: String?
    ): ExportResult =
        jemsPluginRegistry.get(BudgetExportPlugin::class, "standard-budget-export-plugin").export(
            projectId,
            SystemLanguageData.valueOf(exportLanguage.toString()),
            SystemLanguageData.valueOf(inputLanguage.toString()),
            version
        )
}
