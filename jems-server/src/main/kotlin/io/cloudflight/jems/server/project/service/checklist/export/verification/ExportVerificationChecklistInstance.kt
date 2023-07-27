package io.cloudflight.jems.server.project.service.checklist.export.verification

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.export.checklist.ChecklistExportPlugin
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanViewReportVerification
import io.cloudflight.jems.server.project.service.checklist.ChecklistInstancePersistence
import io.cloudflight.jems.server.project.service.checklist.export.ExportChecklistInstanceException
import io.cloudflight.jems.server.project.service.checklist.export.ExportChecklistInstanceNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExportVerificationChecklistInstance(
    private val jemsPluginRegistry: JemsPluginRegistry,
    private val checklistInstancePersistence: ChecklistInstancePersistence,
) : ExportVerificationChecklistInstanceInteractor {

    companion object {
        private const val DEFAULT_CONTROL_CHECKLIST_EXPORT_PLUGIN = "standard-checklist-export-plugin"
    }

    @CanViewReportVerification
    @Transactional(readOnly = true)
    @ExceptionWrapper(ExportChecklistInstanceException::class)
    override fun export(projectId: Long, reportId: Long, checklistId: Long, exportLanguage: SystemLanguage, pluginKey: String?): ExportResult {
        if (!checklistInstancePersistence.existsByIdAndRelatedToId(id = checklistId, relatedToId = reportId))
            throw ExportChecklistInstanceNotFoundException()

        return jemsPluginRegistry.get(ChecklistExportPlugin::class, pluginKey ?: DEFAULT_CONTROL_CHECKLIST_EXPORT_PLUGIN).export(
            projectId = projectId,
            checklistId = checklistId,
            exportLanguage = SystemLanguageData.valueOf(exportLanguage.toString())
        )
    }
}
