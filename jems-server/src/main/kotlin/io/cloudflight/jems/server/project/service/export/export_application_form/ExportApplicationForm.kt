package io.cloudflight.jems.server.project.service.export.export_application_form

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ApplicationFormExportPlugin
import io.cloudflight.jems.plugin.contract.export.ExportResult
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.project.authorization.CanRetrieveProjectForm
import org.springframework.stereotype.Service

@Service
class ExportApplicationForm(private val jemsPluginRegistry: JemsPluginRegistry) : ExportApplicationFormInteractor {

    @ExceptionWrapper(ExportApplicationFormException::class)
    @CanRetrieveProjectForm
    override fun export(
        projectId: Long, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage, version: String?
    ): ExportResult =
        jemsPluginRegistry.get(ApplicationFormExportPlugin::class, "standard-application-form-export-plugin").export(
            projectId,
            SystemLanguageData.valueOf(exportLanguage.toString()),
            SystemLanguageData.valueOf(inputLanguage.toString()),
            version
        )
}
