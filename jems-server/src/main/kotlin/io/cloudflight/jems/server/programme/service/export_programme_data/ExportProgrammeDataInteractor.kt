package io.cloudflight.jems.server.programme.service.export_programme_data

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.plugin.contract.export.ExportResult

interface ExportProgrammeDataInteractor {
    fun export(pluginKey: String?, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage): ExportResult
}
