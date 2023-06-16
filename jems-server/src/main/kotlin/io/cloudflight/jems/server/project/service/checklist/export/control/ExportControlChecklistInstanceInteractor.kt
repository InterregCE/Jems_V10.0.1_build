package io.cloudflight.jems.server.project.service.checklist.export.control

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult

interface ExportControlChecklistInstanceInteractor {

    companion object {
        private const val DEFAULT_CHECKLIST_PLUGIN = "standard-checklist-export-plugin"
    }

    fun export(
        partnerId: Long,
        reportId: Long,
        checklistId: Long,
        exportLanguage: SystemLanguage,
        pluginKey: String? = DEFAULT_CHECKLIST_PLUGIN
    ): ExportResult
}
