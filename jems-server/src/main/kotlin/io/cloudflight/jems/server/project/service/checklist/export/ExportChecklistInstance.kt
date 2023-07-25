package io.cloudflight.jems.server.project.service.checklist.export

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.checklist.ChecklistExportPlugin
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanViewChecklistAssessment
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExportChecklistInstance(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val checklistInstancePersistence: ChecklistInstancePersistence,
) : ExportChecklistInstanceInteractor {

    companion object {
        private const val DEFAULT_CHECKLIST_EXPORT_PLUGIN = "standard-checklist-export-plugin"
    }

    @CanViewChecklistAssessment
    @Transactional(readOnly = true)
    @ExceptionWrapper(ExportChecklistInstanceException::class)
    override fun export(relatedToId: Long, checklistId: Long, exportLanguage: SystemLanguage, pluginKey: String?): ExportResult {
        if (!checklistInstancePersistence.existsByIdAndRelatedToId(id = checklistId, relatedToId = relatedToId))
            throw ExportChecklistInstanceNotFoundException()

        return jemsPluginRegistry.get(ChecklistExportPlugin::class, pluginKey ?: DEFAULT_CHECKLIST_EXPORT_PLUGIN).export(
            projectId = relatedToId,
            checklistId = checklistId,
            exportLanguage = SystemLanguageData.valueOf(exportLanguage.toString())
        )
    }
}
