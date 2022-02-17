package io.cloudflight.jems.server.programme.service.exportProgrammeData

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ProgrammeDataExportPlugin
import io.cloudflight.jems.server.common.exception.ExceptionWrapper
import io.cloudflight.jems.server.plugin.JemsPluginRegistry
import io.cloudflight.jems.server.programme.authorization.CanExportProgrammeData
import org.springframework.stereotype.Service

const val EXPORT_TIMEOUT_IN_MINUTES = 15L

@Service
class ExportProgrammeData(
    private val pluginRegistry: JemsPluginRegistry, private val exportProgrammeDataService: ExportProgrammeDataService
) : ExportProgrammeDataInteractor {

    @CanExportProgrammeData
    @ExceptionWrapper(ExportProgrammeDataException::class)
    override fun export(pluginKey: String?, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage) {
        pluginRegistry.get(ProgrammeDataExportPlugin::class, pluginKey).also {
            synchronized(this) {
                exportProgrammeDataService.saveExportFileMetaData(it.getKey(), exportLanguage, inputLanguage)
            }
        }.also { exportProgrammeDataService.execute(it, exportLanguage, inputLanguage) }
    }
}
