package io.cloudflight.jems.server.programme.service.exportProgrammeData

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage

interface ExportProgrammeDataInteractor {
    fun export(pluginKey: String?, exportLanguage: SystemLanguage, inputLanguage: SystemLanguage)
}
