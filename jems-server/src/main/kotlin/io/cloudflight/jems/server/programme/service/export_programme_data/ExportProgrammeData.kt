package io.cloudflight.jems.server.programme.service.export_programme_data

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ProgrammeDataExportPlugin
import io.cloudflight.jems.plugin.contract.models.common.SystemLanguageData
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.programme.authorization.CanExportProgrammeData
import org.springframework.stereotype.Service

@Service
class ExportProgrammeData(private val pluginRegistry: JemsPluginRegistry) : ExportProgrammeDataInteractor {

    @CanExportProgrammeData
    @ExceptionWrapper(ExportProgrammeDataException::class)
    override fun export(pluginKey: String?, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage) =
        pluginRegistry.get(ProgrammeDataExportPlugin::class, pluginKey).export(
            SystemLanguageData.valueOf(exportLanguage.toString()),
            SystemLanguageData.valueOf(inputLanguage.toString())
        )
}
