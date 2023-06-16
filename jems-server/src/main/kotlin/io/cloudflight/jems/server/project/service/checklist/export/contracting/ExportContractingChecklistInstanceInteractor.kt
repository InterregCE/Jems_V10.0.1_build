package io.cloudflight.jems.server.project.service.checklist.export.contracting

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult

interface ExportContractingChecklistInstanceInteractor {

    companion object {
        private const val DEFAULT_CHECKLIST_PLUGIN = "standard-checklist-export-plugin"
    }

    fun export(
        projectId: Long,
        checklistId: Long,
        exportLanguage: SystemLanguage,
        pluginKey: String? = DEFAULT_CHECKLIST_PLUGIN
    ): ExportResult
}
